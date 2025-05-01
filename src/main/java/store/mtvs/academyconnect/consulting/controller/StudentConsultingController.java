package store.mtvs.academyconnect.consulting.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import store.mtvs.academyconnect.consulting.domain.entity.ConsultingBooking;
import store.mtvs.academyconnect.consulting.dto.*;
import store.mtvs.academyconnect.consulting.service.*;
import store.mtvs.academyconnect.global.config.CustomUserDetails;

import java.time.Clock;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("/student")
@RequiredArgsConstructor
public class StudentConsultingController {

    private final ConsultingBookingService consultingBookingService;
    private final UndefinedConsultingService undefinedConsultingService;
    private final StudentBookingViewService studentBookingViewService;
    private final InstructorInfoService instructorInfoService;
    private final Clock clock;

    /**
     * 학생의 예약용 화면 (S01, S02, S03 기능 통합)
     */
    @GetMapping("/consulting-booking")
    public String booking(
            @RequestParam(required = false) String instructorId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month,
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            Model model) {

        // 학생 ID 설정 (추후 로그인 기능 구현 시 수정 필요)
        String studentId = customUserDetails.getId();
        model.addAttribute("studentId", studentId);

        log.info("상담 예약 페이지 요청: studentId={}, instructorId={}, date={}, year={}, month={}",
                studentId, instructorId, date, year, month);

        try {
            // S01: 강사 목록 조회
            List<InstructorInfoForListDto> instructors = instructorInfoService.getActiveTeachers();
            model.addAttribute("instructors", instructors);

            // 강사 목록이 비어있는지 확인하고, 비어있다면 에러 페이지로 리디렉션
            if (instructors == null || instructors.isEmpty()) {
                log.warn("등록된 강사가 없습니다.");
                model.addAttribute("errorMessage", "등록된 강사가 없습니다.");
                model.addAttribute("backUrl", "/");
                return "error/consulting-student-error";
            }

            // 선택된 강사 ID 결정 (파라미터 > 기본 할당 > 첫 번째 강사)
            String selectedInstructorId = instructorId;
            if (selectedInstructorId == null) {
                selectedInstructorId = instructorInfoService.getAssignedTeacherId(studentId);
                // 할당된 강사도 없으면 첫 번째 강사 선택
                if (selectedInstructorId == null && !instructors.isEmpty()) {
                    selectedInstructorId = instructors.get(0).getId();
                }
            }
            model.addAttribute("selectedInstructorId", selectedInstructorId);

            if (selectedInstructorId != null) {
                // S02: 선택된 강사의 프로필 정보 조회
                InstructorProfileDto selectedInstructor = instructorInfoService.getTeacherProfile(selectedInstructorId);
                model.addAttribute("selectedInstructor", selectedInstructor);

                // 표시할 년월 결정 로직 개선
                LocalDate now = LocalDate.now(clock);

                // 우선순위:
                // 1. URL 파라미터로 받은 year, month
                // 2. 선택된 날짜(date)의 year, month
                // 3. 현재 날짜의 year, month
                int selectedYear;
                int selectedMonth;

                if (year != null && month != null) {
                    // 1. URL 파라미터로 받은 year, month 사용
                    selectedYear = year;
                    selectedMonth = month;
                } else if (date != null) {
                    // 2. 선택된 날짜의 year, month 사용
                    selectedYear = date.getYear();
                    selectedMonth = date.getMonthValue();
                } else {
                    // 3. 현재 날짜의 year, month 사용
                    selectedYear = now.getYear();
                    selectedMonth = now.getMonthValue();
                }

                model.addAttribute("year", selectedYear);
                model.addAttribute("month", selectedMonth);

                // 해당 년월의 정보
                YearMonth yearMonth = YearMonth.of(selectedYear, selectedMonth);
                model.addAttribute("monthName", yearMonth.getMonth().toString());
                model.addAttribute("daysInMonth", yearMonth.lengthOfMonth());
                model.addAttribute("firstDayOfWeek", yearMonth.atDay(1).getDayOfWeek().getValue() % 7);

                // 선택된 날짜 설정 (URL 파라미터만 사용)
                LocalDate selectedDate = date;
                model.addAttribute("selectedDate", selectedDate);
                model.addAttribute("selectedDateStr", selectedDate != null ? selectedDate.toString() : null);

                // 로깅 추가
                log.debug("달력 표시 정보: year={}, month={}, selectedDate={}", selectedYear, selectedMonth, selectedDate);

                // S03: 달력 데이터 생성
                model.addAttribute("calendarData",
                        studentBookingViewService.getCalendarData(selectedInstructorId, selectedYear, selectedMonth, selectedDate));

                // S03: 선택된 날짜의 예약 가능 시간 슬롯 조회
                List<AvailableSlotDto> availableSlots =
                        studentBookingViewService.getAvailableSlots(selectedInstructorId, selectedDate);
                model.addAttribute("availableSlots", availableSlots);
                model.addAttribute("hasAvailableSlots", !availableSlots.isEmpty());

                log.info("상담 예약 페이지 로드 성공: instructorId={}, date={}, year={}, month={}, 예약 가능 슬롯 수={}",
                        selectedInstructorId, selectedDate, selectedYear, selectedMonth, availableSlots.size());
            }

            return "consulting/student/booking";
        } catch (Exception e) {
            // 예외 처리
            log.error("상담 예약 페이지 로드 중 오류 발생: {}", e.getMessage(), e);
            model.addAttribute("errorMessage", "상담 예약 페이지를 불러오는 중 오류가 발생했습니다.");
            model.addAttribute("backUrl", "/");
            return "error/consulting-student-error";
        }
    }

    /**
     * 특정 강사/날짜의 예약 가능 시간 슬롯 조회 API (AJAX용)
     */
    @GetMapping("/api/consulting/teachers/{teacherId}/available-slots")
    @ResponseBody
    public ResponseEntity<List<AvailableSlotDto>> getAvailableSlotsForDate(
            @PathVariable String teacherId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        log.info("예약 가능 시간 슬롯 API 요청: teacherId={}, date={}", teacherId, date);

        try {
            List<AvailableSlotDto> availableSlots = studentBookingViewService.getAvailableSlots(teacherId, date);
            log.info("예약 가능 시간 슬롯 API 응답: {} 개의 슬롯 반환", availableSlots.size());
            return ResponseEntity.ok(availableSlots);
        } catch (Exception e) {
            log.error("예약 가능 시간 슬롯 API 오류: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * 학생의 예약 목록 조회
     *
     * @param view  활성화할 탭 (기본값: upcoming)
     * @param model 모델
     * @return 뷰 이름
     */
    @GetMapping("/consulting-my-bookings")
    public String myBookings(@RequestParam(defaultValue = "upcoming") String view, Model model, @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        // 임시로 고정된 학생 ID 사용 (로그인 구현 후 세션에서 가져오도록 수정 필요)
        String studentId = customUserDetails.getId();
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
     *
     * @param bookingId 취소할 예약 ID
     * @param model     모델
     * @return 리다이렉트 URL 또는 에러 페이지
     */
    @PostMapping("/consulting-bookings/{bookingId}/cancel")
    public String cancelBooking(@PathVariable Long bookingId, Model model, @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        // 임시로 고정된 학생 ID 사용 (로그인 구현 후 세션에서 가져오도록 수정 필요)
        String studentId = customUserDetails.getId();
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

    /**
     * S05: 시간 미지정 상담 요청 처리
     * 학생이 강사를 선택하고 시간 지정 없이 상담을 요청하는 기능
     */
    @PostMapping("/consulting-booking/unspecific")
    public String handleUnspecificBooking(
            @RequestParam("instructorId") String instructorId,
            @RequestParam("studentId") String studentId,
            @RequestParam(value = "message", required = false) String message,
            RedirectAttributes redirectAttributes) {

        try {
            // 유효성 검사
            if (studentId == null || studentId.isEmpty()) {
                redirectAttributes.addFlashAttribute("errorMessage", "학생 정보가 없습니다. 다시 로그인해주세요.");
                return "redirect:/student/consulting-booking?instructorId=" + instructorId;
            }

            UndefinedConsultingRequestDto requestDto = UndefinedConsultingRequestDto.builder()
                    .studentId(studentId)
                    .instructorId(instructorId)
                    .message(message)
                    .build();

            undefinedConsultingService.createConsultationRequest(requestDto);

            redirectAttributes.addFlashAttribute("successMessage", "상담 요청이 성공적으로 등록되었습니다.");
            return "redirect:/student/consulting-my-bookings?view=requests";

        } catch (Exception e) {
            log.error("미지정 상담 요청 처리 중 오류 발생: {}", e.getMessage(), e);
            redirectAttributes.addFlashAttribute("errorMessage", "상담 요청 중 오류가 발생했습니다.");
            return "redirect:/student/consulting-booking?instructorId=" + instructorId;
        }
    }

    @PostMapping("/consulting-booking/specific")
    public String handleSpecificBooking(
            @ModelAttribute StudentBookingRequestDto requestDto,
            RedirectAttributes redirectAttributes,
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {

        // 디버깅: 전달된 데이터 출력
        log.info("Received booking request: instructorId={}, startTime={}, message={}",
                requestDto.getInstructorId() != null ? requestDto.getInstructorId() : "null",
                requestDto.getStartTime() != null ? requestDto.getStartTime() : "null",
                requestDto.getMessage() != null ? requestDto.getMessage() : "null");

        String studentId = customUserDetails.getId();
        try {
            log.debug("BookingService.createBookingFromSlot 호출 시도: studentId={}, requestDto={}",
                    studentId, requestDto);

            ConsultingBooking booking = consultingBookingService.createBookingFromSlot(studentId, requestDto);

            log.info("시간 지정 예약 처리 성공: bookingId={}, studentId={}, instructorId={}, startTime={}",
                    booking.getId(), studentId, requestDto.getInstructorId(), requestDto.getStartTime());

            // 성공 메시지 (예약 시간 포함)
            String successMsg = String.format("%s 강사님과의 %s 예약이 완료되었습니다.",
                    booking.getInstructor().getName(),
                    booking.getStartTime().format(DateTimeFormatter.ofPattern("MM월 dd일 HH:mm")));
            redirectAttributes.addFlashAttribute("successMessage", successMsg);

            // 성공 시 '내 예약 보기'의 '예정된 예약' 탭으로 이동
            return "redirect:/student/consulting-my-bookings?view=upcoming";

        } catch (IllegalArgumentException | IllegalStateException ex) { // 예상된 실패 (예약 불가, 충돌 등)
            log.warn("예약 처리 중 예상된 오류 발생: {}", ex.getMessage(), ex);
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
            // 실패 시 원래 예약 화면으로 돌아가기 (강사 ID 유지)
            return "redirect:/student/consulting-booking?instructorId=" + requestDto.getInstructorId();
        } catch (Exception e) { // 예상치 못한 서버 오류
            log.error("시간 지정 예약 처리 중 예상치 못한 오류 발생", e);
            redirectAttributes.addFlashAttribute("errorMessage", "예약 처리 중 오류가 발생했습니다. 다시 시도해주세요.");
            return "redirect:/student/consulting-booking?instructorId=" + requestDto.getInstructorId();
        }
    }
}