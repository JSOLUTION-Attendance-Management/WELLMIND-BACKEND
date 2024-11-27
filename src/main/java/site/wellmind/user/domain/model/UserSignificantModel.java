package site.wellmind.user.domain.model;

import jakarta.persistence.*;
import lombok.*;
import site.wellmind.transfer.converter.TransferTypeConverter;
import site.wellmind.user.converter.MaritalTypeConverter;
import site.wellmind.user.domain.vo.JobType;
import site.wellmind.user.domain.vo.MaritalType;

import java.util.List;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Setter
@Getter
@Table(name = "jsol_user_significant")
public class UserSignificantModel {
    @Id
    @Column(name = "USER_SIGN_IDX",nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 1. 기혼, 미혼 여부
    @Enumerated(EnumType.STRING)
    @Convert(converter = MaritalTypeConverter.class)
    private MaritalType maritalStatus;

    @Column(name = "USER_IS_SMOKER")
    private Boolean smoker;   // 3. 흡연 여부

    @Column(name = "USER_SLEEP_HOURS")
    private int sleepHours;  // 4. 수면 시간

    @Column(name = "USER_SKIP_BREAKFAST")
    private Boolean skipBreakfast;    // 5. 아침 식사 결식 여부

    // 6. 만성 질환
    @ElementCollection
    @CollectionTable(name = "jsol_chronic_diseases", joinColumns = @JoinColumn(name = "USER_DISEASE_IDX"))
    @Column(name = "USER_DISEASE")
    private List<String> chronicDiseases; // 알레르기성 비염 등 여러 질환 가능


    @Enumerated(EnumType.STRING)
    @Convert(converter = MaritalTypeConverter.class)
    private JobType jobCategory;     // 7. 직군 구분 (현장직, 사무직)



}
