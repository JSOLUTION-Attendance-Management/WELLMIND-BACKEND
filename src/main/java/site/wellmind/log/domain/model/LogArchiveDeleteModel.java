package site.wellmind.log.domain.model;

import jakarta.persistence.*;
import lombok.*;
import site.wellmind.common.domain.model.BaseModel;
import site.wellmind.log.domain.vo.DeleteStatus;
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

    @Column(name = "DELETE_BY_IDX")
    private Long deleteById;

    @Column(name = "DELETE_TYPE")
    private DeleteStatus deleteType;

    @Column(name = "DELETE_DATE")
    private LocalDateTime deleteDate;

    @Column(name = "RESTORE_BY_IDX")
    private Long restoreById;

    @Column(name = "RESTORE_DATE")
    private LocalDateTime restoreDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_IDX",nullable = false)
    private UserTopModel userId;

}
