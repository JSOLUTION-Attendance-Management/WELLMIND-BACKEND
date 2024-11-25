package site.wellmind.user.domain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import site.wellmind.user.domain.model.AdminTopModel;
import site.wellmind.user.domain.model.UserTopModel;

import java.time.LocalDateTime;

// EducationDto 내부 클래스
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EducationDto {
    private Long id;
    private String degree;
    private String major;
    private String institutionName;
    //private UserTopModel userTopModel;
    //private AdminTopModel adminTopModel;
    private Long userId;
    private Long adminId;

    @JsonFormat(shape = JsonFormat.Shape.STRING,pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    private LocalDateTime regDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING,pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    private LocalDateTime modDate;
}
