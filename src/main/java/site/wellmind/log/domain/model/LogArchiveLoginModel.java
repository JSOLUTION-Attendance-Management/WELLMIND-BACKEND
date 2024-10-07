package site.wellmind.log.domain.model;

import jakarta.persistence.*;
import lombok.*;
import site.wellmind.common.domain.model.BaseModel;
import site.wellmind.user.domain.model.UserTopModel;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Setter
@Getter
@Table(name="jsol_logarchive_login")
@ToString(exclude = {"id"})
public class LogArchiveLoginModel extends BaseModel {
    @Id
    @Column(name = "LOGIN_LOG_IDX",nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_IDX",nullable = false)
    private UserTopModel userId;
}
