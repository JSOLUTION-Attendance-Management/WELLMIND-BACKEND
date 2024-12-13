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
import site.wellmind.attend.domain.dto.RecentReportTypesDto;
import site.wellmind.attend.domain.dto.ReportDto;
import site.wellmind.attend.domain.dto.ReportListDto;
import site.wellmind.attend.domain.dto.UpdateReportDto;
import site.wellmind.attend.domain.model.AttendReportModel;
import site.wellmind.attend.domain.model.QAttendReportModel;
import site.wellmind.attend.repository.AttendReportRepository;
import site.wellmind.attend.service.ReportService;
import site.wellmind.common.domain.vo.ExceptionStatus;
import site.wellmind.common.exception.GlobalException;
import site.wellmind.security.util.EncryptionUtil;
import site.wellmind.transfer.domain.model.QTransferModel;
import site.wellmind.transfer.domain.model.TransferModel;
import site.wellmind.user.domain.dto.AccountDto;
import site.wellmind.user.domain.model.AdminTopModel;
import site.wellmind.user.domain.model.UserTopModel;
import site.wellmind.user.repository.AdminTopRepository;
import site.wellmind.user.repository.UserTopRepository;

import java.util.ArrayList;
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
    private final QTransferModel qTransfer = QTransferModel.transferModel;
    private final EncryptionUtil encryptionUtil;

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
    public List<RecentReportTypesDto> viewRecent(AccountDto accountDto, Integer recentCount) {
        BooleanBuilder whereClause = new BooleanBuilder();
        Long currentAccountId = accountDto.getAccountId();
        boolean isAdmin = accountDto.isAdmin();

        if (isAdmin) {
            whereClause.and(qAttendReport.reportedId.eq(currentAccountId)).and(qAttendReport.isAdmin.eq(true).and(qAttendReport.isSent.eq(true)));
        } else {
            whereClause.and(qAttendReport.reportedId.eq(currentAccountId)).and(qAttendReport.isAdmin.eq(false).and(qAttendReport.isSent.eq(true)));
        }

        List<AttendReportModel> reports = queryFactory
                .selectFrom(qAttendReport)
                .where(whereClause)
                .orderBy(qAttendReport.regDate.desc())
                .fetch();

        return reports.stream()
                .map(this::entityToDtoRecentReportTypes)
                .collect(Collectors.toList());
    }

    @Override
    public List<ReportListDto> viewCal(AccountDto accountDto) {
        BooleanBuilder whereClause = new BooleanBuilder();
        Long currentAccountId = accountDto.getAccountId();
        boolean isAdmin = accountDto.isAdmin();

        if (isAdmin) {
            whereClause.and(qAttendReport.reportedId.eq(currentAccountId)).and(qAttendReport.isAdmin.eq(true).and(qAttendReport.isSent.eq(true)));
        } else {
            whereClause.and(qAttendReport.reportedId.eq(currentAccountId)).and(qAttendReport.isAdmin.eq(false).and(qAttendReport.isSent.eq(true)));
        }

        List<AttendReportModel> reports = queryFactory
                .selectFrom(qAttendReport)
                .where(whereClause)
                .orderBy(qAttendReport.regDate.desc())
                .fetch();

        return reports.stream()
                .map(model -> {
                    String reportedEmployeeId = getEmployeeId(model);
                    String reportedEmployeeName = getEmployeeName(model);
                    ReportListDto dto = entityToDtoReportListRecord(model);
                    dto.setReportedEmployeeName(reportedEmployeeName);
                    dto.setReportedEmployeeId(reportedEmployeeId);
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public Page<ReportListDto> viewAd(AccountDto accountDto, Pageable pageable) {
        BooleanBuilder whereClause = new BooleanBuilder();
        Long currentAccountId = accountDto.getAccountId();
        boolean isAdmin = accountDto.isAdmin();

        if (isAdmin) {
            whereClause.and(qAttendReport.reporterId.id.eq(currentAccountId));
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
            if (!reportModel.getReportedId().equals(accountDto.getAccountId()) || !reportModel.getIsAdmin().equals(accountDto.isAdmin()) || reportModel.getIsSent().equals(false)) {
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

        // reportType 변환 및 "빈발형" 추가
        String[] types = reportDto.getReportType().split(",");
        List<String> convertedTypes = new ArrayList<>();
        for (String type : types) {
            switch (type) {
                case "LA":
                    convertedTypes.add("지각");
                    break;
                case "LL":
                    convertedTypes.add("야근");
                    break;
                case "EL":
                    convertedTypes.add("조퇴");
                    break;
                case "OT":
                    convertedTypes.add("외출");
                    break;
                case "BT":
                    convertedTypes.add("출장");
                    break;
            }
        }
        String convertedType = String.join(", ", convertedTypes) + " 빈발형";
        reportDto.setReportType(convertedType);

        if (reportModel.getIsAdmin()) {
            AdminTopModel admin = adminTopRepository.findById(reportModel.getReportedId())
                    .orElseThrow(() -> new GlobalException(ExceptionStatus.DATA_NOT_FOUND, ExceptionStatus.DATA_NOT_FOUND.getMessage()));
            setAdditionalInfo(reportDto, admin);
        } else {
            UserTopModel user = userTopRepository.findById(reportModel.getReportedId())
                    .orElseThrow(() -> new GlobalException(ExceptionStatus.DATA_NOT_FOUND, ExceptionStatus.DATA_NOT_FOUND.getMessage()));
            setAdditionalInfo(reportDto, user);
        }

        return reportDto;
    }

    private void setAdditionalInfo(ReportDto reportDto, AdminTopModel admin) {
        TransferModel latestTransfer = getLatestTransfer(admin.getId(), true);
        reportDto.setDepartmentAndPosition(latestTransfer.getDepartment().getName() + " " + latestTransfer.getPosition().getName());
        reportDto.setHireDate(admin.getUserInfoModel().getHireDate());
        reportDto.setReportedEmployeeIsLong(admin.getUserInfoModel().isLong());
        reportDto.setEmail(admin.getEmail());
        try {
            String decryptedRegNumberFor = encryptionUtil.decrypt(admin.getRegNumberFor());
            reportDto.setBirthDate(convertToBirthDate(decryptedRegNumberFor));
        } catch (Exception e) {
            log.error("Error decrypting regNumberFor: " + e.getMessage());
            reportDto.setBirthDate(null);
        }
    }

    private void setAdditionalInfo(ReportDto reportDto, UserTopModel user) {
        TransferModel latestTransfer = getLatestTransfer(user.getId(), false);
        reportDto.setDepartmentAndPosition(latestTransfer.getDepartment().getName() + " " + latestTransfer.getPosition().getName());
        reportDto.setHireDate(user.getUserInfoModel().getHireDate());
        reportDto.setReportedEmployeeIsLong(user.getUserInfoModel().isLong());
        reportDto.setEmail(user.getEmail());
        try {
            String decryptedRegNumberFor = encryptionUtil.decrypt(user.getRegNumberFor());
            reportDto.setBirthDate(convertToBirthDate(decryptedRegNumberFor));
        } catch (Exception e) {
            log.error("Error decrypting regNumberFor: " + e.getMessage());
            reportDto.setBirthDate(null);
        }
    }

    private TransferModel getLatestTransfer(Long id, boolean isAdmin) {
        return queryFactory
                .selectFrom(qTransfer)
                .where(isAdmin ? qTransfer.adminId.id.eq(id) : qTransfer.userId.id.eq(id))
                .orderBy(qTransfer.regDate.desc())
                .fetchFirst();
    }

    private String convertToBirthDate(String regNumberFor) {
        if (regNumberFor == null || regNumberFor.length() < 6) {
            return null;
        }

        String birthYear = regNumberFor.substring(0, 2);
        String birthMonth = regNumberFor.substring(2, 4);
        String birthDay = regNumberFor.substring(4, 6);

        int year = Integer.parseInt(birthYear);
        int century = (year > 24) ? 1900 : 2000;

        return String.format("%d-%s-%s", century + year, birthMonth, birthDay);
    }

    @Override
    public void updateReport(Long reportId, UpdateReportDto updateReportDto, AccountDto accountDto) {
        AttendReportModel reportModel = attendReportRepository.findById(reportId)
                .orElseThrow(() -> new GlobalException(ExceptionStatus.DATA_NOT_FOUND, ExceptionStatus.DATA_NOT_FOUND.getMessage()));

        // 관할 관리자가 아닌 경우
        if (!accountDto.isAdmin() || !reportModel.getReporterId().getId().equals(accountDto.getAccountId())) {
            throw new GlobalException(ExceptionStatus.UNAUTHORIZED, ExceptionStatus.UNAUTHORIZED.getMessage());
        }

        reportModel.setAiComment(updateReportDto.getAiComment());
        reportModel.setManagerComment(updateReportDto.getManagerComment());

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