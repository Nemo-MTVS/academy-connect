package store.mtvs.academyconnect.consulting.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import store.mtvs.academyconnect.global.config.ClockConfiguration;
import store.mtvs.academyconnect.profile.domain.entity.Profile;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InstructorUndefinedConsultingDto {
    private Long id;
    private String studentName;
    private String studentId;
    private String classGroup;
    private String filePath;
    private String status;
    private String comment;
    private LocalDateTime requestAt;
    private LocalDateTime updatedAt;

    // 날짜 포맷팅 메서드 (뷰에서 사용) - ClockConfiguration 활용
    public String getFormattedRequestDay() {
        return ClockConfiguration.formatDate(requestAt);
    }



    // 상태에 따른 CSS 클래스 반환 (뷰에서 사용)
    public String getStatusClass() {
        if ("WAITING".equals(status)) {
            return "status-badge waiting";
        } else if ("DONE".equals(status)) {
            return "status-badge completed";
        } else {
            return "status-badge waiting";
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

    // 예약 취소 가능 여부 (뷰에서 사용)
    public boolean isAssignable() {
        return "WAITING".equals(status);
    }
}