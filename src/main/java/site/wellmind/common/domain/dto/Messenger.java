package site.wellmind.common.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Messenger
 * <p>Message Data Transfer Object</p>
 *
 * @author Yuri Seok(tjrdbfl)
 * @version 1.0
 * @since 2024-10-08
 */
@Component
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Messenger {
    private String message; // 메시지 내용
    private Object data;    // 성공 여부 등 상태를 나타냄
    private Integer count;  // 응답에 포함된 데이터 개수 등
    private Boolean state;  // 성공 여부 등 상태를 나타냄
}
