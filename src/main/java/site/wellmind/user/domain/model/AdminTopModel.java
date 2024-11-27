package site.wellmind.user.domain.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import site.wellmind.attend.domain.model.AttendQrModel;
import site.wellmind.attend.domain.model.AttendRecordModel;
import site.wellmind.attend.domain.model.AttendReportModel;
import site.wellmind.common.domain.model.BaseModel;
import site.wellmind.common.domain.vo.AdminRole;
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
@Table(name = "jsol_admintop")
@ToString(exclude = {"id","userSignificantModel", "userEduIds", "userInfoModel", "role","transferIds",
        "viewLogIds", "updateLogIds", "loginLogIds", "deleteLogIds", "reportIds"})
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class AdminTopModel extends BaseModel {
    @Id
    @Column(name = "ADMIN_IDX", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ADMIN_EMPLOYEE_ID", unique = true, nullable = false)
    private String employeeId;

    @Column(name = "ADMIN_EMAIL", unique = false, nullable = false)
    //@Column(name = "ADMIN_EMAIL",unique = true,nullable = false)
    private String email;

    @Column(name = "ADMIN_PASSWORD", unique = true)
    @JsonIgnore
    private String password;

    @Column(name = "ADMIN_NAME")
    private String name;

    @Column(name = "REG_NUMBER_FOR", nullable = false)
    private String regNumberFor;
    @Column(name = "REG_NUMBER_LAT", nullable = false)
    private String regNumberLat;

    @Column(name = "ADMIN_PHONE_NUM", length = 50)
    private String phoneNum;

    @Builder.Default
    @Column(name = "AUTH_TYPE", length = 1)
    @Pattern(regexp = "[NM]", message = "authType must be 'N' or 'M'")
    private String authType = "M";

    @Column(name = "ADMIN_ROLE_LEVEL", unique = true, nullable = false)
    private Integer adminRoleLevel;

    @Column(name = "ADMIN_LEVEL_CODE_ID", length = 50)
    private String authAdminLevelCodeId;

    @Builder.Default
    @Column(name = "DELETE_FLAG")
    private Boolean deleteFlag = false;

    // ====================== user ========================

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ROLE_IDX", nullable = false)
    private AccountRoleModel role;

    @OneToMany(mappedBy = "adminTopModel", cascade = CascadeType.ALL, orphanRemoval = true,fetch = FetchType.EAGER)
    @JsonManagedReference // 순환 참조 방지
    private List<UserEducationModel> userEduIds;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true,fetch = FetchType.EAGER)
    @JoinColumn(name = "USERINFO_IDX", referencedColumnName = "USERINFO_IDX")
    private UserInfoModel userInfoModel;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true,fetch = FetchType.EAGER)
    @JoinColumn(name="USER_SIGN_IDX")
    private UserSignificantModel userSignificantModel;

    // ====================== transfer ========================
    @OneToMany(mappedBy = "adminId", cascade = CascadeType.ALL, orphanRemoval = true,fetch = FetchType.EAGER)
    @JsonIgnore // 직렬화에서 제외
    private List<TransferModel> transferIds;

    // ====================== attend ========================
    @OneToMany(mappedBy = "adminId", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore // 직렬화에서 제외
    private List<AttendQrModel> qrTokenIds;

    @OneToMany(mappedBy = "adminId", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore // 직렬화에서 제외
    private List<AttendRecordModel> recordIds;

    // ====================== log ========================
    @OneToMany(mappedBy = "viewerId", cascade = CascadeType.MERGE, orphanRemoval = false)
    @JsonIgnore // 직렬화에서 제외
    private List<LogArchiveViewModel> viewLogIds;

    @OneToMany(mappedBy = "deleterId", cascade = CascadeType.MERGE, orphanRemoval = false)
    @JsonIgnore // 직렬화에서 제외
    private List<LogArchiveDeleteModel> deleteLogIds;

    @OneToMany(mappedBy = "adminId", cascade = CascadeType.MERGE, orphanRemoval = false)
    @JsonIgnore // 직렬화에서 제외
    private List<LogArchiveLoginModel> loginLogIds;

    @OneToMany(mappedBy = "updaterId", cascade = CascadeType.MERGE, orphanRemoval = false)
    @JsonIgnore // 직렬화에서 제외
    private List<LogArchiveUpdateModel> updateLogIds;

    @OneToMany(mappedBy = "reporterId",cascade = CascadeType.MERGE,orphanRemoval = false)
    private List<AttendReportModel> reportIds;

}
