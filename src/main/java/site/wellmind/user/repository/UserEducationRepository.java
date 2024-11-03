package site.wellmind.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import site.wellmind.user.domain.model.UserEducationModel;
@Repository
public interface UserEducationRepository extends JpaRepository<UserEducationModel,Long> {

}
