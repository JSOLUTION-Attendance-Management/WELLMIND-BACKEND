package site.wellmind.log.domain.model;

import jakarta.persistence.*;
import lombok.*;
import site.wellmind.common.domain.model.BaseModel;
import site.wellmind.user.domain.model.AdminTopModel;
import site.wellmind.user.domain.model.UserTopModel;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Setter
@Getter
@Builder
@Table(name="jsol_logarchive_view")
@ToString(exclude = {"id"})
public class LogArchiveViewModel extends BaseModel {
    @Id
    @Column(name = "VIEW_LOG_IDX",nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "VIEW_REASON")
    private String viewReason;

    @Column(name = "VIEWED_IDX",nullable = false)
    private String viewedEmployeeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "VIEWER_IDX",nullable = false)
    private AdminTopModel viewerId;

}
