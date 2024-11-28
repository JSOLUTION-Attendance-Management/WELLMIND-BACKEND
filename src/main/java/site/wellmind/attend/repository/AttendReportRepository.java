package site.wellmind.attend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import site.wellmind.attend.domain.model.AttendReportModel;

@Repository
public interface AttendReportRepository extends JpaRepository<AttendReportModel, Long> {
}
