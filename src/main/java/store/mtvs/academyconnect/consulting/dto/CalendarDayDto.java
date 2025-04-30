package store.mtvs.academyconnect.consulting.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CalendarDayDto {
    private int dayOfMonth;
    private String dateString;  // YYYY-MM-DD 형식
    private boolean isEmpty;    // 해당 월에 속하지 않는 날짜인 경우
    private boolean isPast;     // 오늘보다 이전 날짜인 경우
    private boolean isToday;    // 오늘 날짜인 경우
    private boolean isSelected; // 선택된 날짜인 경우
    private boolean isAvailable; // 예약 가능한 슬롯이 있는 경우
    private boolean isWeekend;  // 주말인 경우
}
