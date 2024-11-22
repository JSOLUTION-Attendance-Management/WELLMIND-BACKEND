package site.wellmind.attend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import site.wellmind.attend.domain.model.AttendRecordModel;

@Repository
public interface AttendRecordRepository extends JpaRepository<AttendRecordModel, Long> {
}