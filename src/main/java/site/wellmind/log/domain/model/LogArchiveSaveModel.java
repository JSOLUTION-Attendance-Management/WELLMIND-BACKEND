package site.wellmind.log.domain.model;

import jakarta.persistence.*;
import lombok.*;
import site.wellmind.user.domain.model.AdminTopModel;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Data
@Builder
@Table(name="jsol_logarchive_save")
@ToString(exclude = {"id"})
public class LogArchiveSaveModel {
    @Id
    @Column(name = "SAVE_LOG_IDX",nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "SAVED_IDX")
    private String savedEmployeeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SAVER_IDX",nullable = false)
    private AdminTopModel saverId;
}
