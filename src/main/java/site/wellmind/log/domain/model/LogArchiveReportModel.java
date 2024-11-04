package site.wellmind.log.domain.model;

import jakarta.persistence.*;
import lombok.*;
import site.wellmind.common.domain.model.BaseModel;
import site.wellmind.user.domain.model.UserTopModel;

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

    @Column(name = "AI_REPORT_COMMENT")
    private String aiComment;

    @Column(name = "MANAGER_REPORT_COMMENT")
    private String managerComment;

    @Column(name = "REPORT_SUMMARY")
    private String summary;

    @Column(name = "MANAGER_IDX")
    private Long managerId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_IDX",nullable = false)
    private UserTopModel userId;
}
