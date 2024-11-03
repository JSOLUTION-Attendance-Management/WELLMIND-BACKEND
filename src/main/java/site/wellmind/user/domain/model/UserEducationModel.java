package site.wellmind.user.domain.model;

import jakarta.persistence.*;
import lombok.*;
import site.wellmind.common.domain.model.BaseModel;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "jsol_user_education")
@Getter
@ToString(exclude = {"id"})
public class UserEducationModel extends BaseModel {

    @Id
    @Column(name = "USER_EDU_IDX",nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "USER_DEGREE")
    private String degree;

    @Column(name = "USER_MAJOR")
    private String major;

    @Column(name = "USER_INSTITUTION_NAME")
    private String institutionName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_IDX", nullable = false)
    private UserTopModel userId;
}
