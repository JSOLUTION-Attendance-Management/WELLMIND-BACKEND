package site.wellmind.security.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import site.wellmind.security.domain.model.AccountTokenModel;
import site.wellmind.security.domain.vo.TokenStatus;

import java.util.List;
import java.util.Optional;

/**
 * AccountTokenRepository
 * <p>refresh token 저장 in MariaDB</p>
 * @since 2024-11-08
 * @version 1.0
 */

@Repository
public interface AccountTokenRepository extends JpaRepository<AccountTokenModel,Long> {

    Optional<AccountTokenModel> findByEmployeeIdAndTokenStatus(String employeeId, TokenStatus tokenStatus);

    List<AccountTokenModel> findByTokenStatusIn(List<TokenStatus> list);

    @Modifying
    @Transactional
    @Query("UPDATE AccountTokenModel a SET a.tokenStatus = :tokenStatus WHERE a.employeeId = :employeeId")
    int updateTokenStatusByEmployeeId(@Param("employeeId") String employeeId, @Param("tokenStatus") TokenStatus tokenStatus);
}
