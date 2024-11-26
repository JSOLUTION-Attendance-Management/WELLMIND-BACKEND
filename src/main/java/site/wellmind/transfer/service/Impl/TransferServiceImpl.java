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
import site.wellmind.transfer.domain.model.QTransferModel;
import site.wellmind.transfer.service.TransferService;
import site.wellmind.user.domain.dto.AccountDto;
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
    private final QTransferModel qTransferModel=QTransferModel.transferModel;
    private final JPAQueryFactory queryFactory;
    @Override
    public Page<TransferDto> findByEmployeeId(Pageable pageable, AccountDto accountDto) {
        if(accountDto.isAdmin()){

//            List<TransferDto> transferDtos=queryFactory.select(
//                    TransferDto.builder()
//                            .id().build()
//            ).from(qTransferModel)
//                    .join();
//            JPAQuery<Long> countQuery = queryFactory
//                    .select(qTransferModel.count())
//                    .from(qTransferModel)
//                    .leftJoin(qUserTop.transferEmployeeIds, qTransfer)
//                    .leftJoin(qTransfer.position, qPosition)
//                    .leftJoin(qTransfer.department, qDepartment)
//                    .where(whereClause);

            //return PageableExecutionUtils.getPage(user, pageable, countQuery::fetchOne);
            return null;
        }
        return null;
    }

    @Override
    public Page<TransferDto> findByAll(String departName, String positionName, String name, Pageable pageable, AccountDto accountDto) {
        return null;
    }
}
