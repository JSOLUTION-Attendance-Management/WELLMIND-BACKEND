package site.wellmind.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import site.wellmind.user.domain.model.UserTopModel;

@Repository
public interface UserTopRepository extends JpaRepository<UserTopModel,Long> {

    Boolean existsByEmployeeId(String employeeId);
}
