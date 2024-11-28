package site.wellmind.user.domain.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import site.wellmind.attend.domain.model.AttendQrModel;
import site.wellmind.attend.domain.model.AttendRecordModel;
import site.wellmind.common.domain.model.BaseModel;
import site.wellmind.log.domain.model.*;
import site.wellmind.transfer.domain.model.TransferModel;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Setter
@Getter
@Table(name = "jsol_usertop")
@ToString(exclude = {"id", "userSignificantModel", "userEduIds", "userInfoModel", "role", "transferEmployeeIds", "qrTokenIds", "recordIds", "loginLogIds"})
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class UserTopModel extends BaseModel {

    @Id
    @Column(name = "USER_IDX", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "USER_EMPLOYEE_ID", unique = true, nullable = false)
    private String employeeId;

    @Column(name = "USER_EMAIL", unique = false, nullable = false)
    private String email;

    @Column(name = "USER_PASSWORD", unique = false)
    @JsonIgnore
    private String password;

    @Column(name = "PASSWORD_EXPIRY")
    private LocalDateTime passwordExpiry;

    @Column(name = "USER_NAME")
    private String name;

    @Column(name = "REG_NUMBER_FOR")
    private String regNumberFor;
    @Column(name = "REG_NUMBER_LAT")
    private String regNumberLat;

    @Column(name = "USER_PHONE_NUM", length = 50)
    private String phoneNum;

    @Builder.Default
    @Column(name = "AUTH_TYPE", length = 1)
    @Pattern(regexp = "[NM]", message = "authType must be 'N' or 'M'")
    private String authType = "N";

    @Builder.Default
    @Column(name = "DELETE_FLAG")
    private Boolean deleteFlag = false;

    // ====================== user ========================

    @OneToMany(mappedBy = "userTopModel", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<UserEducationModel> userEduIds;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true,fetch = FetchType.EAGER)
    @JoinColumn(name = "USERINFO_IDX", referencedColumnName = "USERINFO_IDX")
    private UserInfoModel userInfoModel;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true,fetch = FetchType.EAGER)
    @JoinColumn(name="USER_SIGN_IDX")
    private UserSignificantModel userSignificantModel;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ROLE_IDX", nullable = false)
    private AccountRoleModel role;

    // ====================== transfer ========================
    @OneToMany(mappedBy = "userId", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<TransferModel> transferEmployeeIds;

    // ====================== attend ========================
    @OneToMany(mappedBy = "userId", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore // 직렬화에서 제외
    private List<AttendQrModel> qrTokenIds;

    @OneToMany(mappedBy = "userId", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore // 직렬화에서 제외
    private List<AttendRecordModel> recordIds;

    // ====================== log ========================

    @OneToMany(mappedBy = "userId", cascade = CascadeType.MERGE, orphanRemoval = false)
    @JsonIgnore // 직렬화에서 제외
    private List<LogArchiveLoginModel> loginLogIds;


}
