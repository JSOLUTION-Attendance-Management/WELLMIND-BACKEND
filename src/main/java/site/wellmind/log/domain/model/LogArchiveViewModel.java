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
@Table(name="jsol_logarchive_view")
@ToString(exclude = {"id"})
public class LogArchiveViewModel extends BaseModel {
    @Id
    @Column(name = "VIEW_LOG_IDX",nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "VIEW_REASON")
    private String viewReason;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "VIEWED_IDX", referencedColumnName = "USER_EMPLOYEE_ID",nullable = false)
    private UserTopModel userId;   //조회 대상

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "VIEWER_IDX", referencedColumnName = "ADMIN_EMPLOYEE_ID",nullable = false)
    private AdminTopModel adminId;  //조회한 사람
}
