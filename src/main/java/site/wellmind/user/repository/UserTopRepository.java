package site.wellmind.user.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import site.wellmind.user.domain.model.UserTopModel;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserTopRepository extends JpaRepository<UserTopModel,Long> {

    @EntityGraph(attributePaths = {"userEduIds","userInfoModel"})
    Optional<UserTopModel> findById(Long id);
    @EntityGraph(attributePaths = {"userEduIds", "userInfoModel"})
    List<UserTopModel> findAll();

    Boolean existsByEmployeeId(String employeeId);
}
