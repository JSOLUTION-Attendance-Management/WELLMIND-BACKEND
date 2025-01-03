package site.wellmind.log.domain.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Data
@Builder
@Table(name="jsol_logarchive_delete_detail")
@ToString(exclude = {"id"})
public class LogArchiveDeleteDetailModel {

    @Id
    @Column(name = "DELETE_DETAIL_LOG_IDX",nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "DELETE_TABLE_NAME")
    private String tableName;

    @Column(name = "DELETED_IDX")
    private String deletedEmployeeId;

    @Lob
    @Column(name="DELETED_VALUE", columnDefinition = "LONGTEXT")
    private String deletedValue;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MASTER_DETAIL_LOG_IDX",nullable = true)
    private LogArchiveDeleteModel masterDeleteLogId;
}
