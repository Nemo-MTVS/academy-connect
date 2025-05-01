package store.mtvs.academyconnect.lunchmatching.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import store.mtvs.academyconnect.lunchmatching.dto.ApplyRequestDto;
import store.mtvs.academyconnect.lunchmatching.dto.LunchMatchingStatusResponse;
import store.mtvs.academyconnect.lunchmatching.service.LunchMatchingService;
import store.mtvs.academyconnect.user.service.UserService;

import java.util.List;

@RestController // 이 클래스는 REST API를 처리하는 컨트롤러임 (JSON 반환)
@RequiredArgsConstructor // 생성자 주입 자동 처리
@RequestMapping("/student/lunch")  // 기본 경로: /lunch
public class LunchMatchingController {

    private final LunchMatchingService lunchMatchingService;
    private final UserService userService;

    // 현재 로그인한 사용자의 UUID 반환
    private String getCurrentUserId() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails userDetails) {
            return userService.getUserByLoginId(userDetails.getUsername())
                    .map(user -> user.getId()) // UUID 반환
                    .orElse(null);
        }
        return null;
    }

    /**
     * 점심 매칭 신청 API
     * [POST] /lunch/apply
     *
     * 사용자가 매칭 클래스를 신청하는 기능.
     *
     * @param request ApplyRequestDto (lunchMatchingClassId 포함)
     * @return 성공 시 "신청 완료" 메시지 반환
     */
    @PostMapping("/apply")
    public ResponseEntity<String> apply(@RequestBody ApplyRequestDto request) {
        String student = getCurrentUserId();

        lunchMatchingService.apply(student, request.getLunchMatchingClassId());

        return ResponseEntity.ok("신청 완료");
    }

    /**
     * 점심 매칭 신청 취소 API
     * [POST] /lunch/cancel
     *
     * 사용자가 신청한 매칭을 취소하는 기능.
     *
     * @param request ApplyRequestDto (lunchMatchingClassId 포함)
     * @return 성공 시 "신청 취소 완료" 메시지 반환
     */
    @PostMapping("/cancel")
    public ResponseEntity<String> cancel(@RequestBody ApplyRequestDto request) {
        String student = getCurrentUserId();

        lunchMatchingService.cancel(student, request.getLunchMatchingClassId());

        return ResponseEntity.ok("신청 취소 완료");
    }

    /**
     * 점심 매칭 전체 초기화 API (Soft Delete 방식)
     * [POST] /lunch/reset
     *
     * - 매일 오후 13시에 자동 실행되는 초기화 기능을 수동으로 호출할 때 사용.
     * - 모든 매칭 데이터를 Soft Delete 처리한다.
     *
     * @return 성공 시 "초기화 완료" 메시지 반환
     */
    @PostMapping("/reset")
    public ResponseEntity<String> reset() {
        lunchMatchingService.resetAllMatchings(); // 같은 리셋 로직 재사용
        return ResponseEntity.ok("초기화 완료");
    }

    /**
     * 점심 매칭 현황 조회 API
     * [GET] /lunch/status
     *
     * - 각 매칭 클래스별로 현재 신청 인원 수와
     * - 신청자 이름/전공 리스트를 조회하여 반환한다.
     *
     * @return 매칭 현황 리스트 반환
     */
    @GetMapping("/status")
    public ResponseEntity<List<LunchMatchingStatusResponse>> getLunchMatchingStatus() {
        List<LunchMatchingStatusResponse> statusList = lunchMatchingService.getLunchMatchingStatus();
        return ResponseEntity.ok(statusList);
    }

}