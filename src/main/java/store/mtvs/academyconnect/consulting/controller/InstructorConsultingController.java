package store.mtvs.academyconnect.consulting.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import store.mtvs.academyconnect.consulting.dto.*;
import store.mtvs.academyconnect.consulting.infrastructure.repository.ConsultingSlotRepository;
import store.mtvs.academyconnect.consulting.service.ConsultingBookingService;
import store.mtvs.academyconnect.consulting.service.InstructorSchedulingService;
import store.mtvs.academyconnect.consulting.service.UndefinedConsultingService;
import store.mtvs.academyconnect.consulting.domain.entity.ConsultingBooking;
import store.mtvs.academyconnect.user.dto.UserResponseDTO;
import store.mtvs.academyconnect.user.service.UserService;


import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("/teacher")
@RequiredArgsConstructor
public class InstructorConsultingController {

    private final ConsultingBookingService consultingBookingService;
    private final UndefinedConsultingService undefinedConsultingService;
    private final InstructorSchedulingService instructorSchedulingService;
    private final UserService userService;

    @GetMapping("/scheduling")
    public String scheduling(Model model) {
        // 임시로 고정된 강사 ID 사용 (로그인 구현 후 세션에서 가져오도록 수정 필요)
        String instructorId = "uuid-be-ins";
        log.info("강사 스케줄 조회 요청: instructorId={}", instructorId);

        try {
            // 강사 스케줄 조회
            List<InstructorScheduleDto> slots = instructorSchedulingService.getInstructorSchedule(instructorId);
            model.addAttribute("slots", slots);
            log.info("스케줄 목록 조회 완료: 조회된 예정 예약 수={}", slots.size());

            // 학생 목록 조회
            List<UserResponseDTO> students = userService.getAllUsers();
            model.addAttribute("students", students);
            log.info("학생 목록 조회 완료: 조회된 학생 수={}", students.size());
            return "instructor/schedule";

        } catch (Exception e) {
            // 예외 처리
            log.error("스케줄 조회 중 오류 발생: {}", e.getMessage(), e);
            model.addAttribute("errorMessage", "스케줄 목록을 불러오는 중 오류가 발생했습니다.");
            model.addAttribute("backUrl", "/");
            return "error/consulting-student-error";
        }
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
    public String cancelBooking(@PathVariable Long bookingId, @RequestParam(defaultValue = "schedule") String view, Model model) {
        // 임시로 고정된 강사 ID 사용 (로그인 구현 후 세션에서 가져오도록 수정 필요)
        String instructorId = "uuid-be-ins";
        log.info("예약 취소 요청: bookingId={}, instructorId={}", bookingId, instructorId);

        try {
            consultingBookingService.cancelInstructorBooking(bookingId, instructorId);
            log.info("예약 취소 성공: bookingId={}", bookingId);
            if (view.equals("schedule")) {
                return "redirect:/teacher/scheduling";
            } else {
                return "redirect:/teacher/reservation";
            }
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

    @PostMapping("/consulting/reservation")
    public String handleSpecificBooking(
            @ModelAttribute InstructorBookingDto requestDto,
            RedirectAttributes redirectAttributes) {

        // 디버깅: 전달된 데이터 출력
        log.info("Received booking request: getStudentId={}, startTime={}, slotId={}",
                requestDto.getStudentId() != null ? requestDto.getStudentId() : "null",
                requestDto.getStartTime() != null ? requestDto.getStartTime() : "null",
                requestDto.getSlotId() != null ? requestDto.getSlotId() : "null");

        // 임시로 고정된 강사 ID 사용 (로그인 구현 후 세션에서 가져오도록 수정 필요)
        String instructorId = "uuid-be-ins";

        try {
            log.debug("BookingService.createBookingFromSchedule 호출 시도: instructorId={}, requestDto={}",
                    instructorId, requestDto);

            ConsultingBooking booking = consultingBookingService.createBookingFromSchedule(instructorId, requestDto);

            log.info("시간 지정 예약 처리 성공: bookingId={}, instructorId={}, studentId={}, startTime={}",
                    booking.getId(), instructorId, requestDto.getStudentId(), requestDto.getStartTime());

            // 성공 메시지 (예약 시간 포함)
            String successMsg = String.format("%s 학생과의 %s 예약이 완료되었습니다.",
                    booking.getStudent().getName(),
                    booking.getStartTime().format(DateTimeFormatter.ofPattern("MM월 dd일 HH:mm")));
            redirectAttributes.addFlashAttribute("successMessage", successMsg);

            // 성공 시 '내 예약 보기'의 '예정된 예약' 탭으로 이동
            return "redirect:/teacher/scheduling";

        } catch (IllegalArgumentException | IllegalStateException ex) { // 예상된 실패 (예약 불가, 충돌 등)
            log.warn("예약 처리 중 예상된 오류 발생: {}", ex.getMessage(), ex);
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
            // 실패 시 원래 예약 화면으로 돌아가기 (강사 ID 유지)
            return "redirect:/teacher/scheduling";
        } catch (Exception e) { // 예상치 못한 서버 오류
            log.error("시간 지정 예약 처리 중 예상치 못한 오류 발생", e);
            redirectAttributes.addFlashAttribute("errorMessage", "예약 처리 중 오류가 발생했습니다. 다시 시도해주세요.");
            return "redirect:/teacher/scheduling";
        }
    }

    @PostMapping("/schedule/update")
    public String updateSchedule(
            @RequestParam("createSlots") String createSlotsJson,
            @RequestParam("deleteSlotIds") String deleteSlotIdsJson
    ) {
        // 임시로 고정된 강사 ID 사용 (로그인 구현 후 세션에서 가져오도록 수정 필요)
        String instructorId = "uuid-be-ins";
        try {
            // ObjectMapper로 JSON 문자열을 List<String>으로 변환
            ObjectMapper objectMapper = new ObjectMapper();
            List<String> createSlots = objectMapper.readValue(createSlotsJson, List.class);
            List<Long> deleteSlotIds = objectMapper.readValue(
                    deleteSlotIdsJson, new TypeReference<List<Long>>() {}
            );

            //  createSlots와 deleteSlotIds를 사용하여 DB에 저장하거나 삭제하는 로직 작성
            instructorSchedulingService.createSlot(createSlots, instructorId);
            instructorSchedulingService.deleteSlots(deleteSlotIds, instructorId);

            return "redirect:/teacher/scheduling";
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/teacher/scheduling";
        }
    }
}
