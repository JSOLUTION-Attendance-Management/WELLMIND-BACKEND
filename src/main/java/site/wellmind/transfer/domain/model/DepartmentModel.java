package site.wellmind.transfer.domain.model;

import jakarta.persistence.*;
import lombok.*;
import site.wellmind.common.domain.model.BaseModel;
import site.wellmind.user.domain.model.UserTopModel;

import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Setter
@Getter
@Table(name = "jsol_department")
@ToString(exclude = {"id","subDepartIds","parentDepartId"})
public class DepartmentModel extends BaseModel {

    @Id
    @Column(name = "DEPART_IDX",nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "DEPART_NAME",length = 100)
    private String name;

    @Column(name = "DEPART_DESCRIPTION")
    private String description;


    @Column(name="DEPART_TEL",length = 50)
    private String tel;

    @Column(name = "DEPART_LOCATION")
    private String location;

    @Column(name="DEPART_VERSION")
    private Integer version;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PARENT_DEPART_IDX")
    private DepartmentModel parentDepartId;

    @OneToMany(mappedBy = "parentDepartId",cascade = CascadeType.ALL,orphanRemoval = true)
    private List<DepartmentModel> subDepartIds;

    @OneToMany(mappedBy = "department",cascade = CascadeType.ALL,orphanRemoval = true)
    private List<TransferModel> transferIds;

}
