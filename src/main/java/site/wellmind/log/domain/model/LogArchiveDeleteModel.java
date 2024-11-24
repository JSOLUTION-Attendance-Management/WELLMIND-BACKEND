package site.wellmind.log.domain.model;

import jakarta.persistence.*;
import lombok.*;
import site.wellmind.common.domain.model.BaseModel;
import site.wellmind.log.domain.vo.DeleteStatus;
import site.wellmind.user.domain.model.AdminTopModel;
import site.wellmind.user.domain.model.UserTopModel;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@Setter
@Table(name="jsol_logarchive_delete")
@ToString(exclude = {"id"})
@Builder
public class LogArchiveDeleteModel extends BaseModel{

    @Id
    @Column(name = "DELETE_LOG_IDX",nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "DELETE_TYPE")
    private DeleteStatus deleteType;

    @Column(name="DELETE_REASON")
    private String deleteReason;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DELETER_IDX",nullable = false)
    private AdminTopModel deleterId;

    @OneToMany(mappedBy = "masterDeleteLogId",cascade = CascadeType.MERGE,orphanRemoval = false)
    private List<LogArchiveDeleteDetailModel> logDeleteDetailLogIds;

}
