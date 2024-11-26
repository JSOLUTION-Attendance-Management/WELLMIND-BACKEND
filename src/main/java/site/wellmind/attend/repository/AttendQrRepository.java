package site.wellmind.attend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import site.wellmind.attend.domain.model.AttendQrModel;

@Repository
public interface AttendQrRepository extends JpaRepository<AttendQrModel, Long> {

    @Modifying(clearAutomatically = true)
    @Query("UPDATE AttendQrModel a SET a.qrTokenisLast = false " +
            "WHERE a.qrTokenisLast = true AND " +
            "(a.userId.id IN (SELECT u.id FROM UserTopModel u WHERE u.employeeId = :employeeId) OR " +
            "a.adminId.id IN (SELECT ad.id FROM AdminTopModel ad WHERE ad.employeeId = :employeeId))")
    void updatePreviousQrCodes(@Param("employeeId") String employeeId);

}