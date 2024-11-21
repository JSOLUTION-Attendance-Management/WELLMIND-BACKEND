package site.wellmind.user.domain.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import site.wellmind.attend.domain.model.AttendanceQrModel;
import site.wellmind.attend.domain.model.AttendanceRecordModel;
import site.wellmind.common.domain.model.BaseModel;
import site.wellmind.log.domain.model.*;
import site.wellmind.transfer.domain.model.TransferModel;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Setter
@Getter
@Table(name = "jsol_usertop")@ToString(exclude = {"id", "userEduIds", "userInfoModel", "role", "transferIds", "qrTokenIds", "recordIds",
        "viewLogIds", "updateLogIds", "loginLogIds", "deleteLogIds", "reportLogIds"})
public class UserTopModel extends BaseModel {

    @Id
    @Column(name = "USER_IDX",nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "USER_EMPLOYEE_ID",unique = true,nullable = false)
    private String employeeId;

    @Column(name = "USER_EMAIL",unique = false,nullable = false)
    private String email;

    @Column(name = "USER_PASSWORD", unique = true)
    private String password;

    @Column(name = "USER_NAME")
    private String name;

    @Column(name="REG_NUMBER_FOR",nullable = false)
    private String regNumberFor;
    @Column(name = "REG_NUMBER_LAT",nullable = false)
    private String regNumberLat;

    @Column(name="USER_PHONE_NUM",length = 50)
    private String phoneNum;

    @Builder.Default
    @Column(name="AUTH_TYPE",length = 1)
    @Pattern(regexp = "[NM]",message = "authType must be 'N' or 'M'")
    private String authType="N";

    @Builder.Default
    @Column(name = "DELETE_FLAG")
    private Boolean deleteFlag= false;

    // ====================== user ========================

    @OneToMany(mappedBy = "userTopModel",cascade = CascadeType.ALL,orphanRemoval = true,fetch = FetchType.LAZY)
    private List<UserEducationModel> userEduIds;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USERINFO_IDX",referencedColumnName = "USERINFO_IDX")
    private UserInfoModel userInfoModel;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ROLE_IDX", nullable = false)
    private AccountRoleModel role;

    // ====================== transfer ========================
    @OneToMany(mappedBy = "userId", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TransferModel> transferIds;

    // ====================== attend ========================
    @OneToMany(mappedBy = "userId", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AttendanceQrModel> qrTokenIds;

    @OneToMany(mappedBy = "userId", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AttendanceRecordModel> recordIds;

    // ====================== log ========================
    @OneToMany(mappedBy = "userId",cascade = CascadeType.MERGE,orphanRemoval = false)
    private List<LogArchiveViewModel> viewLogIds;

    @OneToMany(mappedBy = "userId",cascade = CascadeType.MERGE,orphanRemoval = false)
    private List<LogArchiveUpdateModel> updateLogIds;

    @OneToMany(mappedBy = "userId",cascade = CascadeType.MERGE,orphanRemoval = false)
    private List<LogArchiveLoginModel> loginLogIds;

    @OneToMany(mappedBy = "userId",cascade = CascadeType.MERGE,orphanRemoval = false)
    private List<LogArchiveDeleteModel> deleteLogIds;

    @OneToMany(mappedBy = "userId",cascade = CascadeType.MERGE,orphanRemoval = false)
    private List<LogArchiveReportModel> reportLogIds;

}
