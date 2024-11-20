package site.wellmind.log.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import site.wellmind.log.domain.model.LogArchiveViewModel;

@Repository
public interface LogArchiveViewRepository extends JpaRepository<LogArchiveViewModel,Long> {
}
