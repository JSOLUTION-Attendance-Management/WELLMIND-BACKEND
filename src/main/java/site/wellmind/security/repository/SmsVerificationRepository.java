package site.wellmind.security.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import site.wellmind.security.domain.model.SmsVerificationModel;
import site.wellmind.security.domain.vo.RequestStatus;

import java.time.LocalDateTime;

@Repository
public interface SmsVerificationRepository extends JpaRepository<SmsVerificationModel,Long> {

    SmsVerificationModel findFirstByPhoneNumOrderByRegDateDesc(String e164FormatPhoneNumber);

    SmsVerificationModel findFirstByEmployeeIdAndVerificationAndRegDateAfter(String employeeId, RequestStatus verification, LocalDateTime regDate);
}
