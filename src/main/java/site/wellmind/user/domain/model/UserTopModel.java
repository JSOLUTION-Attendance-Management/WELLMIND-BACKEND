package site.wellmind.user.domain.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import site.wellmind.attend.domain.model.AttendanceQrModel;
import site.wellmind.attend.domain.model.AttendanceRecordModel;
import site.wellmind.common.domain.model.BaseModel;
import site.wellmind.common.domain.vo.Role;
import site.wellmind.log.domain.model.*;
import site.wellmind.transfer.domain.model.DepartmentModel;
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
@Table(name = "jsol_usertop")
@ToString(exclude = {"id"})
public class UserTopModel extends BaseModel {

    @Id
    @Column(name = "USER_IDX",nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "USER_EMPLOYEE_ID",unique = true,nullable = false)
    private String employeeId;

    @Column(name = "USER_EMAIL",unique = true,nullable = false)
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

    @Column(name="AUTH_TYPE",length = 1)
    @Pattern(regexp = "[NM]",message = "authType must be 'N' or 'M'")
    private String authType="N";

    @Column(name = "AUTH_USER_LEVEL_CODE_ID")
    private Role authUserLevelCodeId;

    @Column(name = "DELETE_FLAG")
    private Boolean deleteFlag= false;

    @Column(name = "USER_AUTH_TOKEN",length = 2048,nullable = false)
    private String authToken;

    @Column(name = "USER_AUTH_TOKEN_EXPIRE",nullable = false)
    private LocalDateTime authTokenExpire;

    // ====================== user ========================
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "USERINFO_IDX",referencedColumnName = "USERINFO_IDX")
    private UserInfoModel userInfoModel;

    @OneToMany(mappedBy = "userId",cascade = CascadeType.ALL,orphanRemoval = true)
    private List<UserEducationModel> userEduIds;

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
    private List<LogArchiveViewModel> viewLogIds= new ArrayList<>();

    @OneToMany(mappedBy = "userId",cascade = CascadeType.MERGE,orphanRemoval = false)
    private List<LogArchiveUpdateModel> updateLogIds= new ArrayList<>();

    @OneToMany(mappedBy = "userId",cascade = CascadeType.MERGE,orphanRemoval = false)
    private List<LogArchiveLoginModel> loginLogIds= new ArrayList<>();

    @OneToMany(mappedBy = "userId",cascade = CascadeType.MERGE,orphanRemoval = false)
    private List<LogArchiveDeleteModel> deleteLogIds= new ArrayList<>();

    @OneToMany(mappedBy = "userId",cascade = CascadeType.MERGE,orphanRemoval = false)
    private List<LogArchiveReportModel> reportLogIds= new ArrayList<>();

}
