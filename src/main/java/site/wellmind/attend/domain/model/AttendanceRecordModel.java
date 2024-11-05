package site.wellmind.attend.domain.model;


import jakarta.persistence.*;
import lombok.*;
import site.wellmind.attend.domain.vo.AttendanceStatus;
import site.wellmind.common.domain.model.BaseModel;
import site.wellmind.user.domain.model.UserTopModel;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Setter
@Getter
@Table(name = "jsol_attendance_record")
@ToString(exclude = {"id"})
public class AttendanceRecordModel extends BaseModel {
    @Id
    @Column(name = "ATTEND_RECORD_IDX",nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "ATTEND_STATUS")
    private AttendanceStatus attendStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_IDX",nullable = false)
    private UserTopModel userId;

}
