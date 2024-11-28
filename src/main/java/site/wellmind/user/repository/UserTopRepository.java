package site.wellmind.user.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import site.wellmind.user.domain.model.UserTopModel;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserTopRepository extends JpaRepository<UserTopModel, Long> {

    @EntityGraph(attributePaths = {"userEduIds", "userInfoModel"})
    Optional<UserTopModel> findById(Long id);

    @EntityGraph(attributePaths = {"userEduIds", "userInfoModel"})
    List<UserTopModel> findAll();

    @Modifying
    @Transactional
    @Query("UPDATE UserTopModel u SET u.password = :password, u.passwordExpiry = :expiry WHERE u.employeeId = :employeeId")
    int updatePasswordByEmployeeId(@Param("employeeId") String employeeId
            , @Param("password") String password
            , @Param("expiry") LocalDateTime expiry);

    @Modifying
    @Transactional
    @Query("UPDATE UserTopModel u SET  u.password = NULL, u.passwordExpiry = NULL WHERE u.employeeId = :employeeId")
    int updatePasswordExpiry(@Param("employeeId") String employeeId);
    Boolean existsByEmail(String email);

    @Query("SELECT u FROM UserTopModel u WHERE u.employeeId = :employeeId")
    Optional<UserTopModel> findByEmployeeId(String employeeId);
}
