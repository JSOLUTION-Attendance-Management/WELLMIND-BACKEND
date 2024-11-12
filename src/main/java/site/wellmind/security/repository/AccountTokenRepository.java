package site.wellmind.security.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
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
}
