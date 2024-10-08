package site.wellmind.common.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Messenger
 * <p>Message Data Transfer Object</p>
 * @since 2024-10-08
 * @version 1.0
 * @author Yuri Seok(tjrdbfl)
 */
@Component
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Messenger {
    private String message;
    private Boolean state;
    private Integer count;
    private Object data;
}
