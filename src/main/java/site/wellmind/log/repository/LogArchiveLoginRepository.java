package site.wellmind.log.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import site.wellmind.log.domain.model.LogArchiveDeleteModel;
import site.wellmind.log.domain.model.LogArchiveLoginModel;

@Repository
public interface LogArchiveLoginRepository extends JpaRepository<LogArchiveLoginModel,Long> {
}
