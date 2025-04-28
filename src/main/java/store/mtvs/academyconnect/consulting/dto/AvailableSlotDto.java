package store.mtvs.academyconnect.consulting.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AvailableSlotDto {
    private Long slotId;  // consulting_slots.id
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private boolean isSelected;
    
    // 시간만 가져오는 헬퍼 메소드 (뷰에서 사용)
    public String getStartTimeStr() {
        return String.format("%02d:%02d", startTime.getHour(), startTime.getMinute());
    }
    
    public String getEndTimeStr() {
        return String.format("%02d:%02d", endTime.getHour(), endTime.getMinute());
    }
    
    // 시간 표시용 메소드 (10:00 - 11:00 형식)
    public String getTimeRangeStr() {
        return getStartTimeStr() + " - " + getEndTimeStr();
    }
}
