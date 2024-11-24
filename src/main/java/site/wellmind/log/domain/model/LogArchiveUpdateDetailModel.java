package site.wellmind.log.domain.model;

import jakarta.persistence.*;
import lombok.*;
import site.wellmind.user.domain.model.AdminTopModel;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Setter
@Getter
@Builder
@Table(name="jsol_logarchive_update_detail")
@ToString(exclude = {"id"})
public class LogArchiveUpdateDetailModel {
    @Id
    @Column(name = "UPDATE_LOG_DETAIL_IDX",nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Lob
    @Column(name="PREVIOUS_VALUE", columnDefinition = "LONGTEXT")
    private String previousValue;

    @Lob
    @Column(name="NEW_VALUE", columnDefinition = "LONGTEXT")
    private String newValue;

    @Column(name = "UPDATED_IDX")
    private String updatedEmployeeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "UPDATER_IDX",nullable = false)
    private AdminTopModel updaterId;
}
