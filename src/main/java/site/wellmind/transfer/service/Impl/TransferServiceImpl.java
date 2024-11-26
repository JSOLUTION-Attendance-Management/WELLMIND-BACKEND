package site.wellmind.transfer.service.Impl;

import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Service;
import site.wellmind.transfer.domain.dto.TransferDto;
import site.wellmind.transfer.domain.model.QDepartmentModel;
import site.wellmind.transfer.domain.model.QPositionModel;
import site.wellmind.transfer.domain.model.QTransferModel;
import site.wellmind.transfer.service.TransferService;
import site.wellmind.user.domain.dto.AccountDto;
import site.wellmind.user.domain.model.QAdminTopModel;
import site.wellmind.user.domain.model.QUserTopModel;
import site.wellmind.user.repository.UserEducationRepository;
import site.wellmind.user.repository.UserInfoRepository;
import site.wellmind.user.repository.UserTopRepository;
import site.wellmind.user.service.AccountService;

import java.util.List;

/**
 * Transfer Service Implementation
 *
 * @author Yuri Seok(tjrdbfl)
 * @version 1.0
 * @see AccountService
 * @see UserTopRepository
 * @see UserInfoRepository
 * @see UserEducationRepository
 * @since 2024-11-26
 */
@Service
@RequiredArgsConstructor
@Slf4j(topic = "TransferServiceImpl")
public class TransferServiceImpl implements TransferService {

    private final QUserTopModel qUserTopModel = QUserTopModel.userTopModel;
    private final QAdminTopModel qAdminTopModel = QAdminTopModel.adminTopModel;
    private final QTransferModel qTransfer = QTransferModel.transferModel;
    private final JPAQueryFactory queryFactory;

    @Override
    public Page<TransferDto> findByEmployeeId(Pageable pageable, AccountDto accountDto) {
        // 조회 조건 설정 (Admin과 User에 따른 조건 분기)
        BooleanExpression condition = accountDto.isAdmin()
                ? qTransfer.adminId.id.eq(accountDto.getAccountId())
                : qTransfer.userId.id.eq(accountDto.getAccountId());

        // TransferDto 리스트 생성
        List<TransferDto> transfers = queryFactory
                .select(Projections.bean(
                        TransferDto.class,
                        qTransfer.id,
                        qTransfer.transferReason,
                        qTransfer.transferType,
                        qTransfer.department.name.concat(" / ").concat(qTransfer.position.name).as("newPosition"),
                        ExpressionUtils.as(
                                JPAExpressions
                                        .select(qTransfer.department.name.concat("/").concat(qTransfer.position.name))
                                        .from(qTransfer)
                                        .where(qTransfer.userId.id.eq(accountDto.getAccountId())) // 조건 추가
                                        .orderBy(qTransfer.modDate.desc()) // 최신 데이터 정렬
                                        .limit(1), // 반드시 단일 값 반환
                                "previousPosition"
                        )
                        ,
                        qTransfer.regDate,
                        qTransfer.modDate,
                        ExpressionUtils.as(
                                JPAExpressions
                                        .select(qUserTopModel.name)
                                        .from(qUserTopModel)
                                        .where(qTransfer.managerEmployeeId.isNotNull()
                                                .and(qUserTopModel.employeeId.eq(qTransfer.managerEmployeeId))),
                                "managerName"
                        )
                ))
                .from(qTransfer)
                .leftJoin(qUserTopModel).on(qTransfer.managerEmployeeId.eq(qUserTopModel.employeeId)) // Manager가 UserTop일 경우
                .leftJoin(qAdminTopModel).on(qTransfer.managerEmployeeId.eq(qAdminTopModel.employeeId)) // Manager가 AdminTop일 경우
                .where(condition)
                .orderBy(qTransfer.regDate.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // 데이터 개수 계산
        JPAQuery<Long> countQuery = queryFactory
                .select(qTransfer.count())
                .from(qTransfer)
                .where(condition);

        // Page 반환
        return PageableExecutionUtils.getPage(transfers, pageable, countQuery::fetchOne);
    }


    @Override
    public Page<TransferDto> findByAll(String departName, String positionName, String name, Pageable pageable, AccountDto accountDto) {
        return null;
    }
}
