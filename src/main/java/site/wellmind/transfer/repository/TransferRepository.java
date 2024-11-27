package site.wellmind.transfer.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import site.wellmind.transfer.domain.dto.TransferDto;
import site.wellmind.transfer.domain.model.TransferModel;

import java.util.List;

@Repository
public interface TransferRepository extends JpaRepository<TransferModel,Long> {
    @Query("SELECT t " +
            "FROM TransferModel t " +
            "WHERE (:isAdmin = TRUE AND t.adminId.id = :accountId) OR " +
            "(:isAdmin = FALSE AND t.userId.id = :accountId) " +
            "ORDER BY t.regDate DESC")
    List<TransferModel> findTransfersByAccountId(@Param("accountId") Long accountId,
                                                 @Param("isAdmin") Boolean isAdmin);

}

//   "(SELECT CONCAT(t2.department.name, ' / ', t2.position.name) " +
//           " FROM TransferModel t2 " +
//           " WHERE t2.userId.id = :accountId AND t2.modDate < t.modDate " +
//           " ORDER BY t2.modDate DESC " +
//           " LIMIT 1), " + // 이전 포지션 값 (단일 레코드만)