package store.mtvs.academyconnect.consulting.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import store.mtvs.academyconnect.global.config.ClockConfiguration;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MyBookingListItemDto {
    private Long id;
    private String instructorName;
    private String status;
    private String message;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private LocalDateTime createdAt;

    // 날짜 포맷팅 메서드 (뷰에서 사용) - ClockConfiguration 활용
    public String getFormattedStartTime() {
        return ClockConfiguration.formatDate(startTime);
    }
    
    public String getFormattedStartTimeOnly() {
        return ClockConfiguration.formatTime(startTime);
    }

    public String getFormattedEndTimeOnly() {
        return ClockConfiguration.formatTime(endTime);
    }

    public String getFormattedCreatedAt() {
        return ClockConfiguration.formatDateTime(createdAt);
    }
    
    // 메시지가 null일 경우 빈 문자열 반환 (뷰에서 사용)
    public String getSafeMessage() {
        return message != null ? message : "";
    }

    // 상태에 따른 CSS 클래스 반환 (뷰에서 사용)
    public String getStatusClass() {
        switch (status) {
            case "예약됨":
                return "badge bg-primary";
            case "상담완료":
                return "badge bg-success";
            case "취소됨":
                return "badge bg-danger";
            default:
                return "badge bg-secondary";
        }
    }

    // 예약 취소 가능 여부 (뷰에서 사용)
    public boolean isCancellable() {
        return "예약됨".equals(status) && startTime.isAfter(LocalDateTime.now());
    }
}