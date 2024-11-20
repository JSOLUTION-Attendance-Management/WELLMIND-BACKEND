package site.wellmind.log.domain.model;

import jakarta.persistence.*;
import lombok.*;
import site.wellmind.common.domain.model.BaseModel;
import site.wellmind.log.converter.AttendanceStatusConverter;
import site.wellmind.log.domain.vo.ReportAttendanceStatus;
import site.wellmind.user.domain.model.AdminTopModel;
import site.wellmind.user.domain.model.UserTopModel;

import java.util.List;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Setter
@Getter
@Table(name="jsol_logarchive_report")
@ToString(exclude = {"id"})
public class LogArchiveReportModel extends BaseModel {
    @Id
    @Column(name = "REPORT_LOG_IDX",nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "AI_REPORT_COMMENT",length = 10000)
    private String aiComment;

    @Column(name = "MANAGER_REPORT_COMMENT",length = 5000)
    private String managerComment;

    @Column(name = "REPORT_SUMMARY")
    private String summary;

    @Convert(converter = AttendanceStatusConverter.class)
    @Column(name = "REPORT_USER_TYPE")
    private List<ReportAttendanceStatus> userType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "REPORTED_IDX", referencedColumnName = "USER_EMPLOYEE_ID",nullable = false)
    private UserTopModel userId;   //조회 대상

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "REPORTER_IDX", referencedColumnName = "ADMIN_EMPLOYEE_ID",nullable = false)
    private AdminTopModel adminId;  //조회한 사람

}
