package site.wellmind.user.domain.vo;

import jakarta.persistence.Embeddable;
import lombok.*;

@Embeddable
@Setter
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class EducationVO {
    private String institutionName;
    private String major;
    private String degree;
    @Override
    public String toString() {
        return institutionName + " " + major + " " + degree;
    }

}
