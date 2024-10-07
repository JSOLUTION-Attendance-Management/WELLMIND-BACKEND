package site.wellmind.attend.domain.model;

import jakarta.persistence.*;
import lombok.*;
import site.wellmind.common.domain.model.BaseModel;
import site.wellmind.user.domain.model.UserTopModel;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Setter
@Getter
@Table(name = "jsol_attendance_qr")
@ToString(exclude = {"id"})
public class AttendanceQrModel extends BaseModel {
    @Id
    @Column(name = "ATTEND_QR_IDX",nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "QR_TOKEN",length = 2048,nullable = false)
    private String qrToken;

    @Column(name = "QR_TOKEN_EXPIRE",nullable = false)
    private LocalDateTime qrTokenExpire;

    @Column(name = "QR_TOKEN_IS_LAST")
    private Boolean qrTokenisLast=false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_IDX",nullable = false)
    private UserTopModel userId;


}
