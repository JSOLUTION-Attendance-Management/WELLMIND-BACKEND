package site.wellmind.log.domain.model;

import jakarta.persistence.*;
import lombok.*;
import site.wellmind.common.domain.model.BaseModel;
import site.wellmind.log.domain.vo.DeleteStatus;
import site.wellmind.user.domain.model.AdminTopModel;
import site.wellmind.user.domain.model.UserTopModel;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Setter
@Getter
@Table(name="jsol_logarchive_delete")
@ToString(exclude = {"id"})
public class LogArchiveDeleteModel {

    @Id
    @Column(name = "DELETE_LOG_IDX",nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "DELETE_TYPE")
    private DeleteStatus deleteType;

    @Column(name = "ACTION_DATE")
    private LocalDateTime actionDate;

    @Column(name = "DELETED_IDX")
    private String deletedEmployeeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DELETER_IDX",nullable = false)
    private AdminTopModel deleterId;
}
