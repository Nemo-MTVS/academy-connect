package store.mtvs.academyconnect.consulting.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import store.mtvs.academyconnect.consulting.dto.InstructorBookingListItemDto;
import store.mtvs.academyconnect.consulting.dto.MyBookingListItemDto;
import store.mtvs.academyconnect.consulting.service.ConsultingBookingService;


import java.util.List;

@Slf4j
@Controller
@RequestMapping("/instructor")
@RequiredArgsConstructor
public class InstructorConsultingController {

    private final ConsultingBookingService consultingBookingService;

    @GetMapping("/scheduling")
    public String scheduling() {
        return "consulting/instructor/scheduling";
    }

    @GetMapping("/my-info")
    public String settingMyInfo() {return "consulting/instructor/my-info";}

    /**
     * 강사의 예약 목록 조회
     * @param view 조회할 뷰 타입 (upcoming, past)
     * @param model 모델
     * @return 뷰 이름
     */
    @GetMapping("/consulting-bookings")
    public String consultingBookings(@RequestParam(defaultValue = "upcoming") String view, Model model) {
        // 임시로 고정된 학생 ID 사용 (로그인 구현 후 세션에서 가져오도록 수정 필요)
        String instructorId = "uuid-be-ins";
        log.info("예약 목록 조회 요청: instructorId={}, view={}", instructorId, view);

        try {
            List<InstructorBookingListItemDto> bookingList;

            if ("past".equals(view)) {
                log.info("지난 예약 목록 조회 시작");
                bookingList = consultingBookingService.getInstructorPastBookings(instructorId);
                model.addAttribute("activeTab", "past");
            } else {
                log.info("예정된 예약 목록 조회 시작");
                bookingList = consultingBookingService.getInstructorUpcomingBookings(instructorId);
                model.addAttribute("activeTab", "upcoming");
            }

            log.info("예약 목록 조회 완료: 조회된 예약 수={}", bookingList.size());
            model.addAttribute("bookings", bookingList);
            return "consulting/instructor/bookings";
        } catch (IllegalArgumentException e) {
            // 학생을 찾을 수 없는 경우 등의 예외 처리
            log.error("예약 목록 조회 실패: {}", e.getMessage(), e);
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("backUrl", "/");
            return "error/consulting-student-error";
        } catch (Exception e) {
            // 기타 예외 처리
            log.error("예약 목록 조회 중 예기치 않은 오류 발생: {}", e.getMessage(), e);
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
        // 임시로 고정된 학생 ID 사용 (로그인 구현 후 세션에서 가져오도록 수정 필요)
        String instructorId = "uuid-be-ins";
        log.info("예약 취소 요청: bookingId={}, instructorId={}", bookingId, instructorId);

        try {
            consultingBookingService.cancelInstructorBooking(bookingId, instructorId);
            log.info("예약 취소 성공: bookingId={}", bookingId);
            return "redirect:/instructor/consulting-bookings?view=upcoming&success=true";
        } catch (IllegalArgumentException e) {
            // 본인 예약이 아니거나, 이미 취소됐거나, 상담 시작된 경우 등
            log.error("예약 취소 실패: {}", e.getMessage(), e);
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("backUrl", "/instructor/consulting-bookings");
            return "error/consulting-student-error";
        } catch (Exception e) {
            log.error("예약 취소 중 예기치 않은 오류 발생: {}", e.getMessage(), e);
            model.addAttribute("errorMessage", "예약 취소 중 오류가 발생했습니다.");
            model.addAttribute("backUrl", "/instructor/consulting-bookings");
            return "error/consulting-student-error";
        }
    }
}
