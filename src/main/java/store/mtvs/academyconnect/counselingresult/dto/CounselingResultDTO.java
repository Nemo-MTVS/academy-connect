package store.mtvs.academyconnect.counselingresult.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import store.mtvs.academyconnect.counselingresult.domain.entity.CounselingResult;

import java.time.LocalDateTime;

public class CounselingResultDTO {

    @Getter
    @Builder
    public static class Response {
        private final Long id;
        private final String studentId;
        private final String studentName;
        private final String instructorId;
        private final String instructorName;
        private final Long bookingId;
        private final String md;
        private final LocalDateTime counselAt;
        private final LocalDateTime createdAt;
        private final LocalDateTime updatedAt;

        public static Response from(CounselingResult result) {
            return Response.builder()
                    .id(result.getId())
                    .studentId(result.getStudent().getId())
                    .studentName(result.getStudent().getName())
                    .instructorId(result.getInstructor().getId())
                    .instructorName(result.getInstructor().getName())
                    .bookingId(result.getBooking() != null ? result.getBooking().getId() : null)
                    .md(result.getMd())
                    .counselAt(result.getCounselAt())
                    .createdAt(result.getCreatedAt())
                    .updatedAt(result.getUpdatedAt())
                    .build();
        }
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CreateRequest {
        private String studentId;
        private String instructorId;
        private Long bookingId;
        private String md;
        private LocalDateTime counselAt;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class UpdateRequest {
        private String md;
        private LocalDateTime counselAt;
    }
} 