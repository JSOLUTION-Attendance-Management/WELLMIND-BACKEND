package site.wellmind.transfer.domain.model;

import jakarta.persistence.*;
import lombok.*;
import site.wellmind.common.domain.model.BaseModel;

import java.util.List;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Setter
@Getter
@Builder
@Table(name = "jsol_position")
@ToString(exclude = {"id"})
public class PositionModel extends BaseModel {
    @Id
    @Column(name = "POSITION_IDX",nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "POSITION_NAME",length = 100)
    private String name;

    @Column(name = "POSITION_DESCRIPTION")
    private String description;

    @Column(name = "POSITION_VERSION")
    private Integer version;

    @OneToMany(mappedBy = "positionId")
    private List<TransferModel> transferIds;

}
