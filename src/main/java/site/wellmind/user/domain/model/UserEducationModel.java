package site.wellmind.user.domain.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
import site.wellmind.common.domain.model.BaseModel;

@EqualsAndHashCode(callSuper = true)
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "jsol_user_education")
@Data
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
    @JoinColumn(name = "USER_IDX", nullable = true)
    @JsonBackReference // 순환 참조 방지
    private UserTopModel userTopModel;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ADMIN_IDX", nullable = true)
    @JsonBackReference // 순환 참조 방지
    private AdminTopModel adminTopModel;
}
