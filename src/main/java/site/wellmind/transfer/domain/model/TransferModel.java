package site.wellmind.transfer.domain.model;

import jakarta.persistence.*;
import lombok.*;
import site.wellmind.common.domain.model.BaseModel;
import site.wellmind.user.domain.model.AdminTopModel;
import site.wellmind.user.domain.model.UserTopModel;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "jsol_transfer")
@Builder
@Setter
@Getter
@ToString(exclude = {"id"})
public class TransferModel extends BaseModel {
    @Id
    @Column(name = "TRANSFER_IDX",nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_IDX", nullable = true)  // null 허용
    private UserTopModel userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ADMIN_IDX", nullable = true)  // null 허용
    private AdminTopModel adminId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "DEPART_IDX",nullable = false)
    private DepartmentModel department;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "POSITION_IDX",nullable = false)
    private PositionModel position;
}
