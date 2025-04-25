package store.mtvs.academyconnect.consulting.domain.enums;


import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Getter
@Slf4j
public enum UndefinedConsultingState {
    WAITING("대기중"),
    DONE("완료");

    private final String status;

    UndefinedConsultingState(String status){
        this.status = status;
    }

    public static UndefinedConsultingState fromValue(String value) {
        for (UndefinedConsultingState state : UndefinedConsultingState.values()) {
            if (state.getStatus().equals(value)) {
                return state;
            }
        }
        log.error("UndefinedConsultingState 없는 상태값입니다: {}", value);
        throw new IllegalArgumentException("잘못된 요청입니다.");
    }
}
