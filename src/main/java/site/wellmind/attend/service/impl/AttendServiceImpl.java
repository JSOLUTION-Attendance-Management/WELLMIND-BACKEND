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
import site.wellmind.user.domain.model.UserTopModel;
import site.wellmind.user.domain.model.AdminTopModel;
import site.wellmind.common.exception.GlobalException;
import site.wellmind.common.domain.vo.ExceptionStatus;

import java.util.List;
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

    private final AttendRecordRepository attendRecordRepository;
    private final JPAQueryFactory queryFactory;
    private final QAttendRecordModel qAttendRecord = QAttendRecordModel.attendRecordModel;

    @Override
    public Page<AttendDto> findBy(String employeeId, AccountDto accountDto, Pageable pageable, Integer recentCount) {
        BooleanBuilder whereClause = new BooleanBuilder();

        if (employeeId != null && !employeeId.equals(accountDto.getEmployeeId())) {
            if (!accountDto.isAdmin()) {
                throw new GlobalException(ExceptionStatus.UNAUTHORIZED, ExceptionStatus.UNAUTHORIZED.getMessage());
            }
            Long accountId = accountDto.getAccountId();
            whereClause.and(qAttendRecord.userId.id.eq(accountId).or(qAttendRecord.adminId.id.eq(accountId)));
        } else {
            Long accountId = accountDto.getAccountId();
            whereClause.and(qAttendRecord.userId.id.eq(accountId).or(qAttendRecord.adminId.id.eq(accountId)));
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

        List<AttendDto> attendDtos = query.fetch().stream()
                .map(this::entityToDtoAttendRecord)
                .collect(Collectors.toList());

        JPAQuery<Long> countQuery = queryFactory
                .select(qAttendRecord.count())
                .from(qAttendRecord)
                .where(whereClause);

        return PageableExecutionUtils.getPage(attendDtos, pageable, countQuery::fetchOne);
    }

    @Override
    public List<AttendDto> findRecentAttendances(String employeeId, AccountDto accountDto, Integer recentCount) {
        if (employeeId == null) {
            employeeId = accountDto.getEmployeeId();
        }

        if (!accountDto.isAdmin() && !employeeId.equals(accountDto.getEmployeeId())) {
            throw new GlobalException(ExceptionStatus.UNAUTHORIZED, ExceptionStatus.UNAUTHORIZED.getMessage());
        }

        Long accountId = accountDto.getAccountId();
        BooleanBuilder whereClause = new BooleanBuilder();
        whereClause.and(qAttendRecord.userId.id.eq(accountId).or(qAttendRecord.adminId.id.eq(accountId)))
                .and(qAttendRecord.attendStatus.in(AttendStatus.NA, AttendStatus.LA));

        JPAQuery<AttendRecordModel> query = queryFactory
                .selectFrom(qAttendRecord)
                .where(whereClause)
                .orderBy(qAttendRecord.regDate.desc())
                .limit(recentCount);

        return query.fetch().stream()
                .map(this::entityToDtoAttendRecord)
                .collect(Collectors.toList());
    }
}