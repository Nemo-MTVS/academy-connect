package store.mtvs.academyconnect.consulting.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class InstructorUndefinedBookingDto {
    private String studentId; // 선택된 학생 ID (UUID)
    private Long slotId; // 선택된 슬롯 ID
    private Long bookingId; // 요청받은 예약 ID

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime startTime; // 선택된 슬롯의 시작 시간

}