package site.wellmind.transfer.service.Impl;

import com.querydsl.core.BooleanBuilder;
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
import site.wellmind.transfer.domain.model.TransferModel;
import site.wellmind.transfer.repository.TransferRepository;
import site.wellmind.transfer.service.TransferService;
import site.wellmind.user.domain.dto.AccountDto;
import site.wellmind.user.domain.model.AdminTopModel;
import site.wellmind.user.domain.model.QAdminTopModel;
import site.wellmind.user.domain.model.QUserTopModel;
import site.wellmind.user.domain.model.UserTopModel;
import site.wellmind.user.repository.AdminTopRepository;
import site.wellmind.user.repository.UserEducationRepository;
import site.wellmind.user.repository.UserInfoRepository;
import site.wellmind.user.repository.UserTopRepository;
import site.wellmind.user.service.AccountService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

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

    private final TransferRepository transferRepository;
    private final AdminTopRepository adminTopRepository;
    private final UserTopRepository userTopRepository;
    private final QUserTopModel qUserTopModel = QUserTopModel.userTopModel;
    private final QAdminTopModel qAdminTopModel = QAdminTopModel.adminTopModel;
    private final QTransferModel qTransfer = QTransferModel.transferModel;
    private final JPAQueryFactory queryFactory;

    @Override
    public Page<TransferDto> findByEmployeeId(Pageable pageable, AccountDto accountDto) {
        List<TransferModel> transfers = transferRepository.findTransfersByAccountId(accountDto.getAccountId(), accountDto.isAdmin());
        String newPosition = transfers.get(0).getDepartment().getName() + "/" + transfers.get(0).getPosition().getName();

        List<TransferDto> transferDtos = transfers.stream().map(transfer -> {
            String managerName;
            Optional<AdminTopModel> admin = adminTopRepository.findByEmployeeId(transfer.getManagerEmployeeId());
            if (admin.isPresent()) {
                managerName = admin.get().getName();
            } else {
                Optional<UserTopModel> user = userTopRepository.findByEmployeeId(transfer.getManagerEmployeeId());
                managerName = user.get().getName();
            }

            // TransferDto 생성
            return TransferDto.builder()
                    .id(transfer.getId())
                    .transferReason(transfer.getTransferReason())
                    .transferType(transfer.getTransferType())
                    .managerName(managerName)
                    .newPosition(transfer.getDepartment().getName()+" / "+transfer.getPosition().getName())
                    .regDate(transfer.getRegDate())
                    .modDate(transfer.getModDate())
                    .build();
        }).toList();
        // 데이터 개수 계산
        long count = transfers.size();

        log.info("transfers: {}", transfers);
        // Page 반환
        return PageableExecutionUtils.getPage(transferDtos, pageable, () -> count);
    }


    @Override
    public Page<TransferDto> findByAll(String departName, String positionName, String name, Pageable pageable, AccountDto accountDto) {
        BooleanBuilder condition = new BooleanBuilder();

        if (departName != null && !departName.isEmpty()) {
            condition.and(qTransfer.department.name.containsIgnoreCase(departName));
        }
        if (positionName != null && !positionName.isEmpty()) {
            condition.and(qTransfer.position.name.containsIgnoreCase(positionName));
        }
        if (name != null && !name.isEmpty()) {
            condition.and(qTransfer.adminId.name.containsIgnoreCase(name).or(qTransfer.userId.name.containsIgnoreCase(name)));
        }

        List<TransferModel> transfers = queryFactory.selectFrom(qTransfer)
                .where(condition)
                .orderBy(qTransfer.regDate.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        List<TransferDto> transferDtos = transfers.stream().map(transfer -> {
            String managerName;
            List<String> record = findByRecord(transfer.getUserId() == null ? transfer.getAdminId().getId() : transfer.getUserId().getId());

            Optional<AdminTopModel> admin = adminTopRepository.findByEmployeeId(transfer.getManagerEmployeeId());
            if (admin.isPresent()) {
                managerName = admin.get().getName();
            } else {
                Optional<UserTopModel> user = userTopRepository.findByEmployeeId(transfer.getManagerEmployeeId());
                managerName = user.map(UserTopModel::getName).orElse("Unknown Manager");
            }

            // TransferDto 생성
            return TransferDto.builder()
                    .id(transfer.getId())
                    .transferReason(transfer.getTransferReason())
                    .transferType(transfer.getTransferType())
                    .managerName(managerName)
                    .newPosition(record.get(0))
                    .previousPosition(record.get(1))
                    .regDate(transfer.getRegDate())
                    .modDate(transfer.getModDate())
                    .build();
        }).toList();
        // 데이터 개수 계산
        long count = transfers.size();

        log.info("transfers: {}", transfers);
        // Page 반환
        return PageableExecutionUtils.getPage(transferDtos, pageable, () -> count);
    }

    private List<String> findByRecord(Long accountId) {
        List<TransferModel> transfers = queryFactory.selectFrom(qTransfer)
                .where(qTransfer.adminId.id.eq(accountId).or(qTransfer.userId.id.eq(accountId)))
                .orderBy(qTransfer.regDate.desc())
                .limit(2)
                .fetch();
        String newPosition = !transfers.isEmpty()
                ? transfers.get(0).getDepartment().getName() + "/" + transfers.get(0).getPosition().getName()
                : ""; // 데이터가 없을 경우 기본값 설정

        String previousPosition = transfers.size() > 1
                ? transfers.get(1).getDepartment().getName() + "/" + transfers.get(1).getPosition().getName()
                : ""; // 이전 포지션이 없을 경우 기본값 설정
        return Arrays.asList(newPosition, previousPosition);
    }
}
