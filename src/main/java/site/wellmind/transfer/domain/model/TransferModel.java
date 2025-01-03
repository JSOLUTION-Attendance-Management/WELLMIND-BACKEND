package site.wellmind.transfer.domain.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
import site.wellmind.common.domain.model.BaseModel;
import site.wellmind.transfer.converter.TransferTypeConverter;
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
@ToString(exclude = {"id","department","position","userId","adminId"})
public class TransferModel extends BaseModel {
    @Id
    @Column(name = "TRANSFER_IDX",nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "TRANSFER_REASON")
    private String transferReason;


    @Column(name = "TRANSFER_TYPE")
    @Convert(converter = TransferTypeConverter.class)
    private TransferType transferType;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "USER_IDX", nullable = true)  // null 허용
    @JsonBackReference // 순환 참조 방지
    private UserTopModel userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ADMIN_IDX", nullable = true)  // null 허용
    @JsonBackReference // 순환 참조 방지
    private AdminTopModel adminId;

    @Column(name = "MANAGER_EMPLOYEE_IDX")
    private String managerEmployeeId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "DEPART_IDX",nullable = false)
    private DepartmentModel department;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "POSITION_IDX",nullable = false)
    private PositionModel position;
}
