package site.wellmind.security.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import site.wellmind.security.domain.model.PasswordSetupTokenModel;
import site.wellmind.security.domain.vo.TokenStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * PasswordSetupTokenRepository
 * <p>password setup token 저장 in MariaDB</p>
 * @since 2024-11-12
 * @version 1.0
 */
@Repository
public interface PasswordSetupTokenRepository extends JpaRepository<PasswordSetupTokenModel,Long> {
    Optional<Object> findByToken(String token);

    List<PasswordSetupTokenModel> findByTokenStatusIn(List<TokenStatus> list);

    List<PasswordSetupTokenModel> findByExpirationTimeBeforeAndTokenStatus(LocalDateTime now, TokenStatus tokenStatus);
}
