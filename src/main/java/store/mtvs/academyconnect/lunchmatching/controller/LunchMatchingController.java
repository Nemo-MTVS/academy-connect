package store.mtvs.academyconnect.lunchmatching.controller;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import store.mtvs.academyconnect.lunchmatching.dto.ApplyRequestDto;
import store.mtvs.academyconnect.lunchmatching.service.LunchMatchingService;

@RestController // REST API 컨트롤러임을 나타냄 (JSON 형태로 응답)
@RequiredArgsConstructor // 생성자를 통한 의존성 주입 자동 처리
@RequestMapping("/lunch")  // 이 컨트롤러의 기본 URL 경로: /lunch
public class LunchMatchingController {

    private final LunchMatchingService lunchMatchingService;

    /**
     * 점심 매칭 신청 API
     * POST /lunch/apply
     *
     * 요청 본문으로 매칭 클래스 ID(lunchMatchingClassId)만 받음.
     * 사용자 ID는 서버 내부에서 임의로 고정하여 사용한다. (테스트용)
     *
     * @param request ApplyRequestDto: lunchMatchingClassId 포함
     * @return 성공 시 200 OK + "신청 완료" 메시지 반환
     */
    @PostMapping("/apply")
    public ResponseEntity<String> apply(@RequestBody ApplyRequestDto request) {
        // 임시 student 아이디 고정 (세션 없이)
        String student = "uuid-be-001"; // 테스트용

        lunchMatchingService.apply(student, request.getLunchMatchingClassId());

        return ResponseEntity.ok("신청 완료");
    }

    /**
     * 점심 매칭 신청 취소 API
     * POST /lunch/cancel
     *
     * 요청 본문으로 매칭 클래스 ID(lunchMatchingClassId)만 받음.
     * 사용자 ID는 서버 내부에서 임의로 고정하여 사용한다. (테스트용)
     *
     * @param request ApplyRequestDto: lunchMatchingClassId 포함
     * @return 성공 시 200 OK + "신청 취소 완료" 메시지 반환
     */
    @PostMapping("/cancel")
    public ResponseEntity<String> cancel(@RequestBody ApplyRequestDto request) {
        // 임시 student 아이디 고정
        String student = "uuid-be-001";

        lunchMatchingService.cancel(student, request.getLunchMatchingClassId());

        return ResponseEntity.ok("신청 취소 완료");
    }

    /**
     * 점심 매칭 전체 초기화 API (Soft Delete 처리)
     * POST /lunch/reset
     * 매일 오후 13시에 신청 내역을 초기화하는 기능 (수동 호출용)
     *
     * @return 성공 시 200 OK + "초기화 완료" 메시지 반환
     */
    @PostMapping("/reset")
    public ResponseEntity<String> reset() {
        lunchMatchingService.resetAllMatchings(); // 같은 리셋 로직 재사용
        return ResponseEntity.ok("초기화 완료");
    }
}