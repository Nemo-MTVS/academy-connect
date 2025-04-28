package store.mtvs.academyconnect.consulting.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UndefinedConsultingRequestDto {
    private String studentId;
    private String instructorId;
    private String message;
}
