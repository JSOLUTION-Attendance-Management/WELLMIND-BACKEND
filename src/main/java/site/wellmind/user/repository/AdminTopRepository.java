package site.wellmind.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import site.wellmind.user.domain.model.AdminTopModel;

import java.util.Optional;


@Repository
public interface AdminTopRepository extends JpaRepository<AdminTopModel,Long> {
    @Query("SELECT a FROM AdminTopModel a WHERE a.employeeId = :employeeId")
    Optional<AdminTopModel> findByEmployeeId(String employeeId);
}
