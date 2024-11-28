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
import site.wellmind.attend.domain.dto.*;
import site.wellmind.attend.domain.model.*;
import site.wellmind.attend.domain.vo.*;
import site.wellmind.attend.repository.*;
import site.wellmind.attend.service.AttendService;
import site.wellmind.user.domain.dto.AccountDto;
import site.wellmind.common.exception.GlobalException;
import site.wellmind.common.domain.vo.ExceptionStatus;
import site.wellmind.user.domain.model.AdminTopModel;
import site.wellmind.user.domain.model.UserTopModel;
import site.wellmind.user.repository.AdminTopRepository;
import site.wellmind.user.repository.UserTopRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Attend Service Implementation
 * <p>Attend Service Implementation</p>
 *
 * @author Jihyeon Park(jihyeon2525)
 * @version 1.0
 * @see AttendService
 * @see AttendRecordRepository
 * @since 2024-11-21
 */
@Service
@RequiredArgsConstructor
@Slf4j(topic = "AttendServiceImpl")
public class AttendServiceImpl implements AttendService {

    private final UserTopRepository userTopRepository;
    private final AdminTopRepository adminTopRepository;
    private final JPAQueryFactory queryFactory;
    private final QAttendRecordModel qAttendRecord = QAttendRecordModel.attendRecordModel;

    @Override
    public Page<BaseAttendDto> findBy(String employeeId, AccountDto accountDto, Pageable pageable, Integer recentCount) {
        BooleanBuilder whereClause = new BooleanBuilder();
        String currentEmployeeId = accountDto.getEmployeeId();
        Long currentAccountId = accountDto.getAccountId();
        List<BaseAttendDto> attendDtos;

        if (employeeId != null) {
            if (!accountDto.isAdmin()) {
                throw new GlobalException(ExceptionStatus.UNAUTHORIZED, ExceptionStatus.UNAUTHORIZED.getMessage());
            }
            Optional<UserTopModel> user = findUserByEmployeeId(employeeId);
            Optional<AdminTopModel> admin = findAdminByEmployeeId(employeeId);

            if (user.isPresent()) {
                Long accountId = user.get().getId();
                whereClause.and(qAttendRecord.userId.id.eq(accountId));
            } else if (admin.isPresent()) {
                Long accountId = admin.get().getId();
                whereClause.and(qAttendRecord.adminId.id.eq(accountId));
            } else {
                throw new GlobalException(ExceptionStatus.USER_NOT_FOUND, ExceptionStatus.USER_NOT_FOUND.getMessage());
            }

            if (accountDto.getRole().equals("ROLE_ADMIN_UBL_55")) {
                JPAQuery<AttendRecordModel> query = queryFactory
                        .selectFrom(qAttendRecord)
                        .where(whereClause)
                        .orderBy(qAttendRecord.regDate.desc());

                if (recentCount != null) {
                    query.limit(recentCount);
                }

                query.offset(pageable.getOffset())
                        .limit(pageable.getPageSize());

                attendDtos = query.fetch().stream()
                        .map(this::entityToDtoSimpleAttendRecord)
                        .collect(Collectors.toList());

                JPAQuery<Long> countQuery = queryFactory
                        .select(qAttendRecord.count())
                        .from(qAttendRecord)
                        .where(whereClause);

                return PageableExecutionUtils.getPage(attendDtos, pageable, countQuery::fetchOne);

            } else if (!accountDto.getRole().equals("ROLE_ADMIN_UBL_66")) {
                throw new GlobalException(ExceptionStatus.UNAUTHORIZED, ExceptionStatus.UNAUTHORIZED.getMessage());
            }
        } else {
            boolean isAdmin = accountDto.isAdmin();
            if (isAdmin) {
                whereClause.and(qAttendRecord.adminId.id.eq(currentAccountId));
            } else {
                whereClause.and(qAttendRecord.userId.id.eq(currentAccountId));
            }
        }

        JPAQuery<AttendRecordModel> query = queryFactory
                .selectFrom(qAttendRecord)
                .where(whereClause)
                .orderBy(qAttendRecord.regDate.desc());

        if (recentCount != null) {
            query.limit(recentCount);
        }

        query.offset(pageable.getOffset())
                .limit(pageable.getPageSize());

        attendDtos = query.fetch().stream()
                .map(this::entityToDtoAttendRecord)
                .collect(Collectors.toList());

        JPAQuery<Long> countQuery = queryFactory
                .select(qAttendRecord.count())
                .from(qAttendRecord)
                .where(whereClause);

        return PageableExecutionUtils.getPage(attendDtos, pageable, countQuery::fetchOne);
    }

    @Override
    public List<RecentAttendDto> findRecentAttendances(AccountDto accountDto, Integer recentCount) {
        Long accountId = accountDto.getAccountId();
        boolean isAdmin = accountDto.isAdmin();
        BooleanBuilder whereClause = new BooleanBuilder();
        if (isAdmin) {
            whereClause.and(qAttendRecord.adminId.id.eq(accountId)).and(qAttendRecord.attendStatus.in(AttendStatus.NA, AttendStatus.LA));
        } else {
            whereClause.and(qAttendRecord.userId.id.eq(accountId)).and(qAttendRecord.attendStatus.in(AttendStatus.NA, AttendStatus.LA));
        }

        JPAQuery<AttendRecordModel> query = queryFactory
                .selectFrom(qAttendRecord)
                .where(whereClause)
                .orderBy(qAttendRecord.regDate.desc())
                .limit(recentCount);

        return query.fetch().stream()
                .map(this::entityToDtoRecentAttendRecord)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<UserTopModel> findUserByEmployeeId(String employeeId) {
        return userTopRepository.findByEmployeeId(employeeId);
    }

    @Override
    public Optional<AdminTopModel> findAdminByEmployeeId(String employeeId) {
        return adminTopRepository.findByEmployeeId(employeeId);
    }
}