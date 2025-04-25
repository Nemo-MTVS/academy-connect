package store.mtvs.academyconnect.lunchmatching.dto;

public class ApplyRequestDto {

    private String userId;
    private Long lunchMatchingClassId;

    public ApplyRequestDto() {
    }

    public String getUserId() {
        return userId;
    }

    public Long getLunchMatchingClassId() {
        return lunchMatchingClassId;
    }
}
