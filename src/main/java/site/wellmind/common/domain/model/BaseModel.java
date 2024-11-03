package site.wellmind.common.domain.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Getter
public abstract class BaseModel {

    @CreatedDate
    @Column(name = "REG_DATE", updatable = false)
    @JsonFormat(shape=JsonFormat.Shape.STRING,pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    private LocalDateTime regDate;

    @LastModifiedDate
    @Column(name = "MOD_DATE")
    @JsonFormat(shape = JsonFormat.Shape.STRING,pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    private LocalDateTime modDate;

    @PrePersist //엔티티가 처음으로 데이터베이스에 저장되기 전에 실행
    protected void onCreate() {
        regDate=LocalDateTime.now();
        modDate=LocalDateTime.now();
    }

    @PreUpdate //엔티티가 수정되어 데이터베이스에 업데이트되기 전에 실행
    protected void onUpdate(){
        modDate=LocalDateTime.now();
    }

}
