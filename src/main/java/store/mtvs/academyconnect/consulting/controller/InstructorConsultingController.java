package store.mtvs.academyconnect.consulting.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import store.mtvs.academyconnect.consulting.dto.InstructorBookingListItemDto;
import store.mtvs.academyconnect.consulting.dto.InstructorUndefinedConsultingDto;
import store.mtvs.academyconnect.consulting.dto.MyBookingListItemDto;
import store.mtvs.academyconnect.consulting.dto.UndefinedConsultingDto;
import store.mtvs.academyconnect.consulting.service.ConsultingBookingService;
import store.mtvs.academyconnect.consulting.service.UndefinedConsultingService;


import java.util.List;

@Slf4j
@Controller
@RequestMapping("/teacher")
@RequiredArgsConstructor
public class InstructorConsultingController {

    private final ConsultingBookingService consultingBookingService;
    private final UndefinedConsultingService undefinedConsultingService;

    @GetMapping("/scheduling")
    public String scheduling() {
        return "instructor/schedule";
    }

    @GetMapping("/my-info")
    public String settingMyInfo() {return "consulting/instructor/my-info";}

    /**
     * 강사의 예약 목록 조회
     * @param view 조회할 뷰 타입 (upcoming, past)
     * @param model 모델
     * @return 뷰 이름
     */
    @GetMapping("/reservation")
    public String consultingBookings(@RequestParam(defaultValue = "upcoming") String view, Model model) {
        // 임시로 고정된 강사 ID 사용 (로그인 구현 후 세션에서 가져오도록 수정 필요)
        String instructorId = "uuid-be-ins";
        log.info("예약 목록 조회 요청: instructorId={}, view={}", instructorId, view);

        try {
            // 모든 목록 로드
            List<InstructorBookingListItemDto> upcomingBookings = consultingBookingService.getInstructorUpcomingBookings(instructorId);
            List<InstructorBookingListItemDto> pastBookings = consultingBookingService.getInstructorPastBookings(instructorId);
            List<InstructorUndefinedConsultingDto> requestsBookings = undefinedConsultingService.getInstructorConsultationRequests(instructorId);

            model.addAttribute("upcomingBookings", upcomingBookings);
            model.addAttribute("pastBookings", pastBookings);
            model.addAttribute("requestsBookings", requestsBookings);

            // view 파라미터에 따라 activeTab 설정
            model.addAttribute("activeTab", view);
            log.info("예약 목록 조회 완료: 조회된 예정 예약 수={}, 지난 예약 수={}, 예약 요청 수={}", upcomingBookings.size(), pastBookings.size(), requestsBookings.size());

            return "instructor/reservation";
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
    @PostMapping("/consulting/{bookingId}/cancel")
    public String cancelBooking(@PathVariable Long bookingId, Model model) {
        // 임시로 고정된 강사 ID 사용 (로그인 구현 후 세션에서 가져오도록 수정 필요)
        String instructorId = "uuid-be-ins";
        log.info("예약 취소 요청: bookingId={}, instructorId={}", bookingId, instructorId);

        try {
            consultingBookingService.cancelInstructorBooking(bookingId, instructorId);
            log.info("예약 취소 성공: bookingId={}", bookingId);
            return "redirect:/teacher/reservation";
        } catch (IllegalArgumentException e) {
            // 본인 예약이 아니거나, 이미 취소됐거나, 상담 시작된 경우 등
            log.error("예약 취소 실패: {}", e.getMessage(), e);
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("backUrl", "/teacher/reservation");
            return "error/consulting-student-error";
        } catch (Exception e) {
            log.error("예약 취소 중 예기치 않은 오류 발생: {}", e.getMessage(), e);
            model.addAttribute("errorMessage", "예약 취소 중 오류가 발생했습니다.");
            model.addAttribute("backUrl", "/teacher/reservation");
            return "error/consulting-student-error";
        }
    }

    /**
     * 예약 처리
     * @param bookingId 처리할 예약 ID
     * @param model 모델
     * @return 리다이렉트 URL 또는 에러 페이지
     */
    @PostMapping("/consulting/{bookingId}/confirm")
    public String confirmBooking(@PathVariable Long bookingId, Model model) {
        // 임시로 고정된 강사 ID 사용 (로그인 구현 후 세션에서 가져오도록 수정 필요)
        String instructorId = "uuid-be-ins";
        log.info("예약 처리 요청: bookingId={}, instructorId={}", bookingId, instructorId);

        try {
            consultingBookingService.confirmInstructorBooking(bookingId, instructorId);
            log.info("예약 처리 성공: bookingId={}", bookingId);
            return "redirect:/teacher/reservation";
        } catch (IllegalArgumentException e) {
            // 본인 예약이 아니거나, 이미 취소됐거나, 상담 시작된 경우 등
            log.error("예약 처리 실패: {}", e.getMessage(), e);
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("backUrl", "/teacher/reservation");
            return "error/consulting-student-error";
        } catch (Exception e) {
            log.error("예약 처리 중 예기치 않은 오류 발생: {}", e.getMessage(), e);
            model.addAttribute("errorMessage", "예약 처리 중 오류가 발생했습니다.");
            model.addAttribute("backUrl", "/teacher/reservation");
            return "error/consulting-student-error";
        }
    }
}
