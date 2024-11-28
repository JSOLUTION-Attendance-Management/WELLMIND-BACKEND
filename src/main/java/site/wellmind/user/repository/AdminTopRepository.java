package site.wellmind.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import site.wellmind.user.domain.model.AdminTopModel;

import java.time.LocalDateTime;
import java.util.Optional;


@Repository
public interface AdminTopRepository extends JpaRepository<AdminTopModel,Long> {
    @Query("SELECT a FROM AdminTopModel a WHERE a.employeeId = :employeeId")
    Optional<AdminTopModel> findByEmployeeId(String employeeId);
    @Modifying
    @Transactional
    @Query("UPDATE AdminTopModel a SET a.password = :password, a.passwordExpiry = :expiry WHERE a.employeeId = :employeeId")
    int updatePasswordByEmployeeId(@Param("employeeId") String employeeId, @Param("password") String password, @Param("expiry") LocalDateTime expiry);
    @Modifying
    @Transactional
    @Query("UPDATE AdminTopModel a SET a.password = NULL, a.passwordExpiry = NULL WHERE a.employeeId = :employeeId")
    int updatePasswordExpiry(@Param("employeeId") String employeeId);
}
