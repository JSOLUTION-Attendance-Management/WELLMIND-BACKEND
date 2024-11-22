package site.wellmind.log.domain.model;

import jakarta.persistence.*;
import lombok.*;
import site.wellmind.common.domain.model.BaseModel;
import site.wellmind.user.domain.model.AdminTopModel;
import site.wellmind.user.domain.model.UserTopModel;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Setter
@Getter
@Table(name="jsol_logarchive_update")
@ToString(exclude = {"id"})
public class LogArchiveUpdateModel extends BaseModel{
    @Id
    @Column(name = "UPDATE_LOG_IDX",nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Lob
    @Column(name="PREVIOUS_VALUE")
    private String previousValue;

    @Lob
    @Column(name="NEW_VALUE")
    private String newValue;

    @Column(name = "UPDATED_IDX")
    private String updatedEmployeeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "UPDATER_IDX",nullable = false)
    private AdminTopModel updaterId;

}
