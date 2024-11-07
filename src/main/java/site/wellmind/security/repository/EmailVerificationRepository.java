package site.wellmind.security.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import site.wellmind.security.domain.model.EmailVerificationModel;

import java.util.Optional;

@Repository
public interface EmailVerificationRepository extends JpaRepository<EmailVerificationModel,Long> {

    Optional<EmailVerificationModel> findByEmail(String email);
}
