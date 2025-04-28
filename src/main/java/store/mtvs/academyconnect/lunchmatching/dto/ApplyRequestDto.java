package store.mtvs.academyconnect.lunchmatching.dto;

/**
 * 점심 매칭 신청 요청 데이터를 담는 DTO 클래스.
 *
 * 사용자가 신청할 매칭 클래스 ID(lunchMatchingClassId)를 전달할 때 사용된다.
 * userId는 세션에서 별도로 관리하므로 포함하지 않는다.
 */
public class ApplyRequestDto {

    private Long lunchMatchingClassId;

    public ApplyRequestDto() {
    }

    public Long getLunchMatchingClassId() {
        return lunchMatchingClassId;
    }
}
