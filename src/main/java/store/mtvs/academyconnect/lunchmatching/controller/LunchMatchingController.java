package store.mtvs.academyconnect.lunchmatching.controller;

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
     * 요청 본문으로 사용자 ID와 매칭 클래스 ID를 받음
     *
     * @param request ApplyRequestDto: userId, lunchMatchingClassId 포함
     * @return 성공 시 200 OK + "신청 완료" 메시지 반환
     */
    @PostMapping("/apply")
    public ResponseEntity<String> apply(@RequestBody ApplyRequestDto request) {
        // 서비스 계층에 신청 처리 위임
        lunchMatchingService.apply(
                request.getUserId(),
                request.getLunchMatchingClassId()
        );

        // 정상 응답 반환
        return ResponseEntity.ok("신청 완료");
    }
}