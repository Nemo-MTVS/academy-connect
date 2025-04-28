package store.mtvs.academyconnect.consulting.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import store.mtvs.academyconnect.consulting.dto.MyBookingListItemDto;
import store.mtvs.academyconnect.consulting.dto.UndefinedConsultingDto;
import store.mtvs.academyconnect.consulting.service.ConsultingBookingService;
import store.mtvs.academyconnect.consulting.service.UndefinedConsultingService;
import java.time.Clock;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("/student")
@RequiredArgsConstructor
public class StudentConsultingController {

    private final ConsultingBookingService consultingBookingService;
    private final UndefinedConsultingService undefinedConsultingService;
    private final Clock clock;

    // 학생의 예약용 화면
    @GetMapping("/consulting-booking")
    public String booking() {
        return "consulting/student/booking";
    }

    /**
     * 학생의 예약 목록 조회
     * @param view 활성화할 탭 (기본값: upcoming)
     * @param model 모델
     * @return 뷰 이름
     */
    @GetMapping("/consulting-my-bookings")
    public String myBookings(@RequestParam(defaultValue = "upcoming") String view, Model model) {
        // 임시로 고정된 학생 ID 사용 (로그인 구현 후 세션에서 가져오도록 수정 필요)
        String studentId = "uuid-be-001";
        log.info("예약 목록 조회 요청: studentId={}, view={}", studentId, view);

        try {
            // 모든 목록 로드
            List<MyBookingListItemDto> upcomingBookings = consultingBookingService.getUpcomingBookings(studentId);
            List<MyBookingListItemDto> pastBookings = consultingBookingService.getPastBookings(studentId);
            List<UndefinedConsultingDto> consultationRequests = undefinedConsultingService.getConsultationRequests(studentId);

            model.addAttribute("upcomingBookings", upcomingBookings);
            model.addAttribute("pastBookings", pastBookings);
            model.addAttribute("consultationRequests", consultationRequests);

            // view 파라미터에 따라 activeTab 설정
            model.addAttribute("activeTab", view);

            return "consulting/student/my-bookings";
        } catch (Exception e) {
            // 예외 처리
            log.error("예약 목록 조회 중 오류 발생: {}", e.getMessage(), e);
            model.addAttribute("errorMessage", "예약 목록을 불러오는 중 오류가 발생했습니다.");
            model.addAttribute("backUrl", "/");
            return "error/consulting-student-error";
        }
    }

    /**
     * 예약 취소
     * @param bookingId 취소할 예약 ID
     * @param model 모델
     * @return 리다이렉트 URL 또는 에러 페이지
     */
    @PostMapping("/consulting-bookings/{bookingId}/cancel")
    public String cancelBooking(@PathVariable Long bookingId, Model model) {
        // 임시로 고정된 학생 ID 사용 (로그인 구현 후 세션에서 가져오도록 수정 필요)
        String studentId = "uuid-be-001";
        log.info("예약 취소 요청: bookingId={}, studentId={}", bookingId, studentId);

        try {
            consultingBookingService.cancelBooking(bookingId, studentId);
            log.info("예약 취소 성공: bookingId={}", bookingId);

            // 1. 플래시 어트리뷰트를 사용하여 리다이렉트 후 한 번만 처리되도록 수정
            // RedirectAttributes를 파라미터로 추가하고 FlashAttribute 사용
            return "redirect:/student/consulting-my-bookings?view=upcoming&success=true";
        } catch (Exception e) {
            // 예외 처리 로직
            log.error("예약 취소 중 오류 발생: {}", e.getMessage(), e);
            model.addAttribute("errorMessage", "예약 취소 중 오류가 발생했습니다.");
            model.addAttribute("backUrl", "/student/consulting-my-bookings");
            return "error/consulting-student-error";
        }
    }
}