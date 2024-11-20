package site.wellmind.log.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import site.wellmind.log.domain.model.LogArchiveDeleteModel;

@Repository
public interface LogArchiveDeleteRepository extends JpaRepository<LogArchiveDeleteModel,Long> {
}
