package store.mtvs.academyconnect.counselingresult.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import store.mtvs.academyconnect.counselingresult.dto.CounselingResultDTO;
import store.mtvs.academyconnect.counselingresult.service.CounselingResultService;
import store.mtvs.academyconnect.counselingresult.domain.entity.CounselingResult;
import store.mtvs.academyconnect.counselingresult.infrastructure.repository.CounselingResultRepository;
import store.mtvs.academyconnect.user.infrastructure.repository.UserRepository;
import store.mtvs.academyconnect.consulting.infrastructure.repository.ConsultingBookingRepository;
import store.mtvs.academyconnect.user.domain.entity.User;
import store.mtvs.academyconnect.consulting.domain.entity.ConsultingBooking;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import store.mtvs.academyconnect.global.config.CustomUserDetails;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

@Slf4j
@Controller
@RequestMapping("/teacher")
@RequiredArgsConstructor
public class CounselingResultController {
    private final CounselingResultRepository counselingResultRepository;
    private final UserRepository userRepository;
    private final ConsultingBookingRepository consultingBookingRepository;
    private final CounselingResultService counselingResultService;

    /**
     * 저장된 전체 상담 결과 목록 조회
     */
    @GetMapping("/counselingresults")
    public String counselingResults(Model model) {
        log.info("상담 결과 목록 조회 요청");

        try {
            List<CounselingResult> results = counselingResultRepository.findAllWithUsers();
            model.addAttribute("counselingResults", results);
            log.info("상담 결과 목록 조회 완료: 조회된 결과 수={}", results.size());
            return "instructor/counselingresults";
        } catch (Exception e) {
            log.error("상담 결과 목록 조회 중 오류 발생: {}", e.getMessage(), e);
            model.addAttribute("errorMessage", "상담 결과 목록을 불러오는 중 오류가 발생했습니다.");
            model.addAttribute("backUrl", "/teacher");
            return "error/consulting-student-error";
        }
    }

    /**
     * 상담 결과 상세 조회 (수정 페이지)
     */
    @GetMapping("/counselingresult/{resultId}/edit")
    public String editCounselingResultForm(@PathVariable Long resultId, Model model) {
        log.info("상담 결과 수정 폼 요청: resultId={}", resultId);

        try {
            // Get counseling result with eager loading
            CounselingResult result = counselingResultRepository.findByIdWithUsers(resultId)
                .orElseThrow(() -> new NoSuchElementException("상담 결과를 찾을 수 없습니다."));
            
            // Get recent bookings for dropdown
            LocalDateTime now = LocalDateTime.now();
            List<ConsultingBooking> bookings = consultingBookingRepository.findRecentBookings(now.minusWeeks(1), now);
            
            model.addAttribute("result", result);
            model.addAttribute("bookings", bookings);
            
            return "instructor/counselingresult-edit";
        } catch (NoSuchElementException e) {
            log.error("상담 결과를 찾을 수 없음: {}", e.getMessage(), e);
            model.addAttribute("errorMessage", "해당 상담 결과를 찾을 수 없습니다.");
            model.addAttribute("backUrl", "/teacher/counselingresults");
            return "error/consulting-student-error";
        } catch (Exception e) {
            log.error("상담 결과 수정 폼 로딩 중 오류 발생: {}", e.getMessage(), e);
            model.addAttribute("errorMessage", "상담 결과 수정 폼을 불러오는 중 오류가 발생했습니다.");
            model.addAttribute("backUrl", "/teacher/counselingresults");
            return "error/consulting-student-error";
        }
    }

    /**
     * 상담 결과 수정
     */
    @PostMapping("/counselingresult/{resultId}")
    public String updateCounselingResult(
            @PathVariable Long resultId,
            @RequestParam String md,
            Model model) {
        
        log.info("상담 결과 수정 요청: resultId={}", resultId);

        try {
            counselingResultService.update(resultId, new CounselingResultDTO.UpdateRequest(md));
            log.info("상담 결과 수정 완료: resultId={}", resultId);
            return "redirect:/teacher/counselingresults";
        } catch (NoSuchElementException e) {
            log.error("상담 결과를 찾을 수 없음: {}", e.getMessage(), e);
            model.addAttribute("errorMessage", "해당 상담 결과를 찾을 수 없습니다.");
            model.addAttribute("backUrl", "/teacher/counselingresults");
            return "error/consulting-student-error";
        } catch (Exception e) {
            log.error("상담 결과 수정 중 오류 발생: {}", e.getMessage(), e);
            model.addAttribute("errorMessage", "상담 결과 수정 중 오류가 발생했습니다.");
            model.addAttribute("backUrl", "/teacher/counselingresults");
            return "error/consulting-student-error";
        }
    }

    /**
     * 상담 결과 삭제
     */
    @DeleteMapping("/counselingresult/{resultId}")
    public String deleteCounselingResult(@PathVariable Long resultId, Model model) {
        log.info("상담 결과 삭제 요청: resultId={}", resultId);

        try {
            counselingResultService.delete(resultId);
            log.info("상담 결과 삭제 완료: resultId={}", resultId);
            return "redirect:/teacher/counselingresults";
        } catch (NoSuchElementException e) {
            log.error("상담 결과를 찾을 수 없음: {}", e.getMessage(), e);
            model.addAttribute("errorMessage", "해당 상담 결과를 찾을 수 없습니다.");
            model.addAttribute("backUrl", "/teacher/counselingresults");
            return "error/consulting-student-error";
        } catch (Exception e) {
            log.error("상담 결과 삭제 중 오류 발생: {}", e.getMessage(), e);
            model.addAttribute("errorMessage", "상담 결과 삭제 중 오류가 발생했습니다.");
            model.addAttribute("backUrl", "/teacher/counselingresults");
            return "error/consulting-student-error";
        }
    }

    /**
     * 새 상담 결과 생성 폼
     */
    @GetMapping("/counselingresult/new")
    public String createCounselingResultForm(Model model) {
        log.info("새 상담 결과 생성 폼 요청");
        try {
            // 예약 목록 조회 (최근 일주일)
            LocalDateTime now = LocalDateTime.now();
            List<ConsultingBooking> bookings = consultingBookingRepository.findRecentBookings(now.minusWeeks(1), now);
            model.addAttribute("bookings", bookings);
            
            return "instructor/counselingresult-create";
        } catch (Exception e) {
            log.error("상담 결과 생성 폼 로딩 중 오류 발생: {}", e.getMessage(), e);
            model.addAttribute("errorMessage", "상담 결과 생성 폼을 불러오는 중 오류가 발생했습니다.");
            model.addAttribute("backUrl", "/teacher/counselingresults");
            return "error/consulting-student-error";
        }
    }

    /**
     * 새 상담 결과 생성
     */
    @PostMapping("/counselingresult")
    public String createCounselingResult(
            @RequestParam String studentName,
            @RequestParam(required = false) Long bookingId,
            @RequestParam String md,
            @AuthenticationPrincipal CustomUserDetails instructor,
            Model model) {
        
        log.info("상담 결과 생성 요청: studentName={}, bookingId={}", studentName, bookingId);

        try {
            // Find student by name
            List<User> matchingStudents = userRepository.findAllByName(studentName);
            if (matchingStudents.isEmpty()) {
                log.error("학생을 찾을 수 없음: {}", studentName);
                model.addAttribute("errorMessage", "해당 이름의 학생을 찾을 수 없습니다.");
                model.addAttribute("backUrl", "/teacher/counselingresults");
                return "error/consulting-student-error";
            }

            User student;
            if (matchingStudents.size() > 1) {
                // For multiple students with same name, use the first one for now
                // In a real implementation, you might want to add student ID to the form
                log.warn("동일한 이름의 학생이 여러명 있음: {}", studentName);
                student = matchingStudents.get(0);
            } else {
                student = matchingStudents.get(0);
            }
            log.info("✅ Selected student: {} (UUID: {})", student.getName(), student.getId());

            // Get instructor user from repository
            User instructorUser = userRepository.findById(instructor.getId())
                .orElseThrow(() -> new IllegalStateException("Instructor not found"));

            // Handle booking and counselAt
            ConsultingBooking booking = null;
            LocalDateTime counselAt = null;
            if (bookingId != null) {
                booking = consultingBookingRepository.findById(bookingId)
                    .orElse(null);
                if (booking != null) {
                    counselAt = booking.getStartTime();
                    log.info("✅ Found booking with ID: {}", bookingId);
                    log.info("✅ Using counseling time: {}", counselAt);
                }
            }

            // Create and save counseling result
            CounselingResult counselingResult = CounselingResult.createCounselingResult(
                student,
                instructorUser,
                booking,
                md,
                counselAt
            );

            log.info("Saving to database...");
            counselingResult = counselingResultRepository.save(counselingResult);
            counselingResultRepository.flush();
            log.info("✅ Created counseling result with ID: {}", counselingResult.getId());

            return "redirect:/teacher/counselingresults";
        } catch (Exception e) {
            log.error("상담 결과 생성 중 오류 발생: {}", e.getMessage(), e);
            model.addAttribute("errorMessage", "상담 결과 생성 중 오류가 발생했습니다.");
            model.addAttribute("backUrl", "/teacher/counselingresults");
            return "error/consulting-student-error";
        }
    }
}
