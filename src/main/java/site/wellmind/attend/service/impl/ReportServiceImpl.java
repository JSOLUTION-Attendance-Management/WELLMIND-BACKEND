package site.wellmind.attend.service.impl;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Service;
import site.wellmind.attend.domain.dto.ReportDto;
import site.wellmind.attend.domain.dto.ReportListDto;
import site.wellmind.attend.domain.dto.UpdateReportDto;
import site.wellmind.attend.domain.model.AttendReportModel;
import site.wellmind.attend.domain.model.QAttendReportModel;
import site.wellmind.attend.repository.AttendReportRepository;
import site.wellmind.attend.service.ReportService;
import site.wellmind.common.domain.vo.ExceptionStatus;
import site.wellmind.common.exception.GlobalException;
import site.wellmind.user.domain.dto.AccountDto;
import site.wellmind.user.domain.model.AdminTopModel;
import site.wellmind.user.domain.model.UserTopModel;
import site.wellmind.user.repository.AdminTopRepository;
import site.wellmind.user.repository.UserTopRepository;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Attend Service Implementation
 * <p>Attend Service Implementation</p>
 *
 * @author Jihyeon Park(jihyeon2525)
 * @version 1.0
 * @see ReportService
 * @see AttendReportRepository
 * @since 2024-11-21
 */
@Service
@RequiredArgsConstructor
@Slf4j(topic = "AttendServiceImpl")
public class ReportServiceImpl implements ReportService {
    private final JPAQueryFactory queryFactory;
    private final QAttendReportModel qAttendReport = QAttendReportModel.attendReportModel;
    private final AdminTopRepository adminTopRepository;
    private final UserTopRepository userTopRepository;
    private final AttendReportRepository attendReportRepository;

    private String getEmployeeName(AttendReportModel model) {
        if (model.getIsAdmin()) {
            return adminTopRepository.findById(model.getReportedId())
                    .map(AdminTopModel::getName)
                    .orElse("Unknown");
        } else {
            return userTopRepository.findById(model.getReportedId())
                    .map(UserTopModel::getName)
                    .orElse("Unknown");
        }
    }

    private String getEmployeeId(AttendReportModel model) {
        if (model.getIsAdmin()) {
            return adminTopRepository.findById(model.getReportedId())
                    .map(AdminTopModel::getEmployeeId)
                    .orElse("Unknown");
        } else {
            return userTopRepository.findById(model.getReportedId())
                    .map(UserTopModel::getEmployeeId)
                    .orElse("Unknown");
        }
    }

    @Override
    public Page<ReportListDto> view(String employeeId, AccountDto accountDto, Pageable pageable) {
        BooleanBuilder whereClause = new BooleanBuilder();
        Long currentAccountId = accountDto.getAccountId();
        String currentEmployeeId = accountDto.getEmployeeId();
        boolean isAdmin = accountDto.isAdmin();

        if (isAdmin) {
            if (employeeId != null && employeeId.equals(currentEmployeeId)) {
                //관리자가 본인 관할의 리포트를 모두 조회하는 경우
                whereClause.and(qAttendReport.reporterId.id.eq(currentAccountId));
            } else if (employeeId == null) {
                // 관리자가 본인의 리포트를 조회하는 경우
                whereClause.and(qAttendReport.reportedId.eq(currentAccountId)).and(qAttendReport.isAdmin.eq(true).and(qAttendReport.isSent.eq(true)));
            } else {
                throw new GlobalException(ExceptionStatus.UNAUTHORIZED, ExceptionStatus.UNAUTHORIZED.getMessage());
            }
        } else if (employeeId == null) {
            // 일반 사용자는 본인의 리포트만 조회 가능
            whereClause.and(qAttendReport.reportedId.eq(currentAccountId)).and(qAttendReport.isAdmin.eq(false).and(qAttendReport.isSent.eq(true)));
        } else {
            throw new GlobalException(ExceptionStatus.UNAUTHORIZED, ExceptionStatus.UNAUTHORIZED.getMessage());
        }

        JPAQuery<AttendReportModel> query = queryFactory
                .selectFrom(qAttendReport)
                .where(whereClause)
                .orderBy(qAttendReport.regDate.desc());

        query.offset(pageable.getOffset())
                .limit(pageable.getPageSize());

        List<ReportListDto> reportListDtos = query.fetch().stream()
                .map(model -> {
                    String reportedEmployeeId = getEmployeeId(model);
                    String reportedEmployeeName = getEmployeeName(model);
                    ReportListDto dto = entityToDtoReportListRecord(model);
                    dto.setReportedEmployeeName(reportedEmployeeName);
                    dto.setReportedEmployeeId(reportedEmployeeId);
                    return dto;
                })
                .collect(Collectors.toList());

        JPAQuery<Long> countQuery = queryFactory
                .select(qAttendReport.count())
                .from(qAttendReport)
                .where(whereClause);

        return PageableExecutionUtils.getPage(reportListDtos, pageable, countQuery::fetchOne);
    }

    @Override
    public ReportDto viewDetail(String employeeId, Long reportId, AccountDto accountDto) {
        AttendReportModel reportModel = attendReportRepository.findById(reportId)
                .orElseThrow(() -> new GlobalException(ExceptionStatus.DATA_NOT_FOUND, ExceptionStatus.DATA_NOT_FOUND.getMessage()));

        if (employeeId == null) {
            // 본인 report 조회 시
            if (!reportModel.getReportedId().equals(accountDto.getAccountId()) || reportModel.getIsSent().equals(false)) {
                // 본인 report가 아니거나 전송되지 않은 report를 상세조회하려고 할 때
                throw new GlobalException(ExceptionStatus.UNAUTHORIZED, ExceptionStatus.UNAUTHORIZED.getMessage());
            }
        } else {
            // 관리자가 본인 관할 report 상세조회 시
            if (!accountDto.isAdmin() || !reportModel.getReporterId().getId().equals(accountDto.getAccountId())) {
                // 다른 관리자 관할의 report를 상세조회하려고 할 때
                throw new GlobalException(ExceptionStatus.UNAUTHORIZED, ExceptionStatus.UNAUTHORIZED.getMessage());
            }
        }

        String reportedEmployeeId = getEmployeeId(reportModel);
        String reportedEmployeeName = getEmployeeName(reportModel);
        ReportDto reportDto = entityToDtoReportRecord(reportModel);
        reportDto.setReportedEmployeeId(reportedEmployeeId);
        reportDto.setReportedEmployeeName(reportedEmployeeName);

        return reportDto;
    }

    @Override
    public void updateReport(Long reportId, UpdateReportDto updateReportDto, AccountDto accountDto) {
        AttendReportModel reportModel = attendReportRepository.findById(reportId)
                .orElseThrow(() -> new GlobalException(ExceptionStatus.DATA_NOT_FOUND, ExceptionStatus.DATA_NOT_FOUND.getMessage()));

        // 관리자가 아닌 경우
        if (!accountDto.isAdmin() || !reportModel.getReporterId().getId().equals(accountDto.getAccountId())) {
            throw new GlobalException(ExceptionStatus.UNAUTHORIZED, ExceptionStatus.UNAUTHORIZED.getMessage());
        }

        // 코멘트 업데이트
        reportModel.setAiComment(updateReportDto.getAiComment());
        reportModel.setManagerComment(updateReportDto.getManagerComment());

        // 리포트 저장
        attendReportRepository.save(reportModel);
    }

    @Override
    public void markReportAsSent(Long reportId, AccountDto accountDto) {
        AttendReportModel reportModel = attendReportRepository.findById(reportId)
                .orElseThrow(() -> new GlobalException(ExceptionStatus.DATA_NOT_FOUND, ExceptionStatus.DATA_NOT_FOUND.getMessage()));

        if (!accountDto.isAdmin() || !reportModel.getReporterId().getId().equals(accountDto.getAccountId())) {
            throw new GlobalException(ExceptionStatus.UNAUTHORIZED, ExceptionStatus.UNAUTHORIZED.getMessage());
        }

        reportModel.setIsSent(true);
        attendReportRepository.save(reportModel);
    }

}