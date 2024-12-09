package site.wellmind.attend.domain.model;

import jakarta.persistence.*;
import lombok.*;
import site.wellmind.common.domain.model.BaseModel;
import site.wellmind.user.domain.model.AdminTopModel;

@Builder
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Setter
@Getter
@Table(name="jsol_attendance_report")
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

    //@Convert(converter = ReportStatusConverter.class)
    @Column(name = "REPORT_USER_TYPE")
    private String userType;

    @Column(name = "REPORTED_IDX",nullable = false)
    private Long reportedId;

    @Column(name = "REPORTED_IS_ADMIN")
    private Boolean isAdmin;

    @Builder.Default
    @Column(name = "IS_SENT")
    private Boolean isSent = false;

    @Column(name = "KEYWORDS")
    private String keywords;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "REPORTER_IDX", referencedColumnName = "ADMIN_IDX")
    private AdminTopModel reporterId;

}