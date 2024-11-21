package site.wellmind.user.domain.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import lombok.*;
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
@ToString(exclude = {"id", "userEduIds", "userInfoModel", "role",
        "viewLogIds", "updateLogIds", "loginLogIds", "deleteLogIds", "reportLogIds"})
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
    private List<UserEducationModel> userEduIds;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "USERINFO_IDX", referencedColumnName = "USERINFO_IDX")
    private UserInfoModel userInfoModel;

    // ====================== transfer ========================
    @OneToMany(mappedBy = "adminId", cascade = CascadeType.ALL, orphanRemoval = true,fetch = FetchType.EAGER)
    private List<TransferModel> transferIds;

    // ====================== log ========================
    @OneToMany(mappedBy = "adminId", cascade = CascadeType.MERGE, orphanRemoval = false)
    private List<LogArchiveViewModel> viewLogIds;

    @OneToMany(mappedBy = "adminId", cascade = CascadeType.MERGE, orphanRemoval = false)
    private List<LogArchiveReportModel> reportLogIds;

    @OneToMany(mappedBy = "adminId", cascade = CascadeType.MERGE, orphanRemoval = false)
    private List<LogArchiveDeleteModel> deleteLogIds;

    @OneToMany(mappedBy = "adminId", cascade = CascadeType.MERGE, orphanRemoval = false)
    private List<LogArchiveLoginModel> loginLogIds;

    @OneToMany(mappedBy = "adminId", cascade = CascadeType.MERGE, orphanRemoval = false)
    private List<LogArchiveUpdateModel> updateLogIds;

}
