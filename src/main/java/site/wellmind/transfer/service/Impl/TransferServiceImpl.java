package site.wellmind.transfer.service.Impl;

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

    // QUERY DSL 사용해서 찾기
    private final QUserTopModel qUserTop=QUserTopModel.userTopModel;
    private final QTransferModel qTransfer=QTransferModel.transferModel;
    private final QDepartmentModel qDepartmentModel=QDepartmentModel.departmentModel;
    private final QPositionModel qPosition=QPositionModel.positionModel;
    private final JPAQueryFactory queryFactory;
    @Override
    public Page<TransferDto> findByEmployeeId(Pageable pageable, AccountDto accountDto) {
        if(accountDto.isAdmin()){

//            List<TransferDto> transfers = queryFactory
//                    .select(TransferDto.builder()
//                            .id(qTransfer.id)
//                            .transferReason(qTransfer.transferReason)
//                            .transferType(qTransfer.transferType)
//                            .regDate(qTransfer.regDate)
//                            .modDate(qTransfer.modDate)
//                            .previousPosition(qTransfer.department.name+" / "+qTransfer.position.name)
//                            .newPosition(qTransfer.department.name+" / "+qTransfer.position.name)
//                            .build())
//                    .from(qTransfer)
//                    .join(qTransfer.userId, qUserTop).on(qUserTop.id.eq(accountDto.getAccountId()))
//                    .join(qTransfer.department, qDepartmentModel)
//                    .join(qTransfer.position, qPosition)
//                    .orderBy(qTransfer.regDate.desc()) // 정렬 조건 추가
//                    .offset(pageable.getOffset())
//                    .limit(pageable.getPageSize())
//                    .fetch();
//
//            JPAQuery<Long> countQuery = queryFactory
//                    .select(qTransfer.count())
//                    .from(qTransfer)
//                    .leftJoin(qUserTop.transferEmployeeIds, qTransfer)
//                    .leftJoin(qTransfer.position, qPosition)
//                    .leftJoin(qTransfer.department, qDepartmentModel)
//                    .where(whereClause);

            return PageableExecutionUtils.getPage(user, pageable, countQuery::fetchOne);
            //return null;
        }
        return null;
    }

    @Override
    public Page<TransferDto> findByAll(String departName, String positionName, String name, Pageable pageable, AccountDto accountDto) {
        return null;
    }
}
