package site.wellmind.security.domain.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.LastModifiedDate;
import site.wellmind.common.domain.model.BaseModel;
import site.wellmind.security.domain.vo.RequestStatus;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Setter
@Getter
@Table(name = "jsol_sms_verification")
@ToString(exclude = {"id"})
public class SmsVerificationModel extends BaseModel {
    @Id
    @Column(name = "SMS_VERIFY_IDX",nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "SMS_VERIFY_PHONE_NUM", length = 50)
    private String phoneNum;

    @Column(name = "SMS_VERFIY_KEY")
    private String verifyKey;

    @Column(name="SMS_VERIFY_VERIFICATION")
    private RequestStatus verification;

    @Column(name = "SMS_VERIFY_EMPLOYEE_ID",nullable = false)
    private String employeeId;

    @Builder.Default
    @Column(name = "SMS_VERIFY_REQUEST_COUNT", nullable = false)
    private Integer requestCount = 0;

    @Column(name = "SMS_VERIFY_LAST_REQUEST_TIME")
    @JsonFormat(shape = JsonFormat.Shape.STRING,pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    private LocalDateTime lastRequestTime;
}
