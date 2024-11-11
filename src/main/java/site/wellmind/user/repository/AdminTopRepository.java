package site.wellmind.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import site.wellmind.user.domain.model.AdminTopModel;

import java.util.Optional;


@Repository
public interface AdminTopRepository extends JpaRepository<AdminTopModel,Long> {

    Optional<AdminTopModel> findByEmployeeId(String employeeId);
}
