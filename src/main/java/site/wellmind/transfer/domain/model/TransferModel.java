package site.wellmind.transfer.domain.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
import site.wellmind.common.domain.model.BaseModel;
import site.wellmind.transfer.domain.vo.TransferType;
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

    @Column(name = "TRANSFER_REASON")
    private String transferReason;

    @Column(name = "TRANSFER_TYPE")
    private TransferType transferType;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "USER_IDX", nullable = true)  // null 허용
    @JsonBackReference // 순환 참조 방지
    private UserTopModel userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ADMIN_IDX", nullable = true)  // null 허용
    @JsonBackReference // 순환 참조 방지
    private AdminTopModel adminId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MANAGER_IDX", nullable = true)  // 상사의 직원 ID
    private UserTopModel managerId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "DEPART_IDX",nullable = false)
    private DepartmentModel department;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "POSITION_IDX",nullable = false)
    private PositionModel position;
}
