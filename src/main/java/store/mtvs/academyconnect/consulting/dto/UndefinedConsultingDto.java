package store.mtvs.academyconnect.consulting.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UndefinedConsultingDto {
    private Long id;
    private String instructorName;
    private String status;
    private String comment;
    private LocalDateTime requestAt;
    private LocalDateTime updatedAt;

    // 상태에 따른 CSS 클래스 반환 (뷰에서 사용)
    public String getStatusClass() {
        if ("WAITING".equals(status)) {
            return "badge bg-warning text-dark";
        } else if ("DONE".equals(status)) {
            return "badge bg-success";
        } else {
            return "badge bg-secondary";
        }
    }
    
    // 상태 한글화 (뷰에서 사용)
    public String getFormattedStatus() {
        if ("WAITING".equals(status)) {
            return "대기중";
        } else if ("DONE".equals(status)) {
            return "시간배정완료";
        } else {
            return status;
        }
    }
}