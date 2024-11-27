package site.wellmind.user.domain.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import site.wellmind.user.converter.MaritalTypeConverter;
import site.wellmind.user.domain.vo.JobType;
import site.wellmind.user.domain.vo.MaritalType;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserSignificantDto {
    private Long id;
    private String maritalStatus;
    private Boolean smoker;
    private int sleepHours;
    private Boolean skipBreakfast;
    private List<String> chronicDiseases;
    private String jobCategory;
}
