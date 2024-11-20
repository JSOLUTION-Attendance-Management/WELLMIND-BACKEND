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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DELETED_IDX", referencedColumnName = "USER_EMPLOYEE_ID",nullable = false)
    private UserTopModel userId;   //조회 대상

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DELETER_IDX", referencedColumnName = "ADMIN_EMPLOYEE_ID",nullable = false)
    private AdminTopModel adminId;  //조회한 사람

}
