package site.wellmind.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import site.wellmind.user.domain.model.UserInfoModel;

import java.util.Optional;

@Repository
public interface UserInfoRepository extends JpaRepository<UserInfoModel,Long> {
}
