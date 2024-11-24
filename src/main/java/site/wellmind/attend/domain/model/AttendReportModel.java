package site.wellmind.attend.domain.model;

import jakarta.persistence.*;
import lombok.*;
import site.wellmind.common.domain.model.BaseModel;
import site.wellmind.attend.converter.ReportStatusConverter;
import site.wellmind.attend.domain.vo.ReportStatus;
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
public class AttendReportModel extends BaseModel {
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

    @Convert(converter = ReportStatusConverter.class)
    @Column(name = "REPORT_USER_TYPE")
    private List<ReportStatus> userType;

    @Column(name = "REPORTED_IDX",nullable = false)
    private String reportedEmployeeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "REPORTER_IDX", referencedColumnName = "ADMIN_EMPLOYEE_ID")
    private AdminTopModel reporterId;  //조회한 사람

}