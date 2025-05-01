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
    public String editCounselingResult(@PathVariable Long resultId, Model model) {
        log.info("상담 결과 상세 조회 요청: resultId={}", resultId);

        try {
            CounselingResultDTO.Response result = counselingResultService.findById(resultId);
            model.addAttribute("counselingResult", result);
            return "instructor/counselingresult-edit";
        } catch (NoSuchElementException e) {
            log.error("상담 결과를 찾을 수 없음: {}", e.getMessage(), e);
            model.addAttribute("errorMessage", "해당 상담 결과를 찾을 수 없습니다.");
            model.addAttribute("backUrl", "/teacher/counselingresults");
            return "error/consulting-student-error";
        } catch (Exception e) {
            log.error("상담 결과 상세 조회 중 오류 발생: {}", e.getMessage(), e);
            model.addAttribute("errorMessage", "상담 결과를 불러오는 중 오류가 발생했습니다.");
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
            @RequestParam(required = false) LocalDateTime counselAt,
            Model model) {
        log.info("상담 결과 수정 요청: resultId={}", resultId);

        try {
            CounselingResultDTO.UpdateRequest updateRequest = CounselingResultDTO.UpdateRequest.builder()
                    .md(md)
                    .counselAt(counselAt)
                    .build();
                    
            counselingResultService.update(resultId, updateRequest);
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
     * 새 상담 결과 생성
     */
    @PostMapping("/counselingresult")
    public String createCounselingResult(
            @RequestParam String studentId,
            @RequestParam(required = false) Long bookingId,
            @RequestParam String md,
            @RequestParam(required = false) LocalDateTime counselAt,
            Model model) {
        // 임시로 고정된 강사 ID 사용 (로그인 구현 후 세션에서 가져오도록 수정 필요)
        String instructorId = "uuid-be-ins";
        
        log.info("상담 결과 생성 요청: studentId={}, instructorId={}", studentId, instructorId);

        try {
            CounselingResultDTO.CreateRequest createRequest = CounselingResultDTO.CreateRequest.builder()
                    .studentId(studentId)
                    .instructorId(instructorId)
                    .bookingId(bookingId)
                    .md(md)
                    .counselAt(counselAt)
                    .build();
            
            counselingResultService.create(createRequest);
            log.info("상담 결과 생성 완료");
            return "redirect:/teacher/counselingresults";
        } catch (NoSuchElementException e) {
            log.error("상담 결과 생성 실패 - 사용자를 찾을 수 없음: {}", e.getMessage(), e);
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("backUrl", "/teacher/counselingresults");
            return "error/consulting-student-error";
        } catch (Exception e) {
            log.error("상담 결과 생성 중 오류 발생: {}", e.getMessage(), e);
            model.addAttribute("errorMessage", "상담 결과 생성 중 오류가 발생했습니다.");
            model.addAttribute("backUrl", "/teacher/counselingresults");
            return "error/consulting-student-error";
        }
    }
}
