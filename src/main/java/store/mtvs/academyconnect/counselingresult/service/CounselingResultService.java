package store.mtvs.academyconnect.counselingresult.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import store.mtvs.academyconnect.consulting.domain.entity.ConsultingBooking;
import store.mtvs.academyconnect.consulting.infrastructure.repository.ConsultingBookingRepository;
import store.mtvs.academyconnect.counselingresult.domain.entity.CounselingResult;
import store.mtvs.academyconnect.counselingresult.dto.CounselingResultDTO;
import store.mtvs.academyconnect.counselingresult.infrastructure.repository.CounselingResultRepository;
import store.mtvs.academyconnect.user.domain.entity.User;
import store.mtvs.academyconnect.user.infrastructure.repository.UserRepository;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CounselingResultService {

    private final CounselingResultRepository counselingResultRepository;
    private final UserRepository userRepository;
    private final ConsultingBookingRepository consultingBookingRepository;

    // 상담 id 로 결과 조회
    public CounselingResultDTO.Response findById(Long id) {
        return CounselingResultDTO.Response.from(
                counselingResultRepository.findByIdWithUsers(id)
                        .orElseThrow(() -> new NoSuchElementException("Counseling result not found"))
        );
    }

    // 학생 ID로 상담 결과 조회
    public List<CounselingResultDTO.Response> findByStudentId(String studentId) {
        return counselingResultRepository.findByStudentIdWithUsers(studentId)
                .stream()
                .map(CounselingResultDTO.Response::from)
                .collect(Collectors.toList());
    }

    // 강사 ID로 상담 결과 조회
    public List<CounselingResultDTO.Response> findByInstructorId(String instructorId) {
        return counselingResultRepository.findByInstructorIdWithUsers(instructorId)
                .stream()
                .map(CounselingResultDTO.Response::from)
                .collect(Collectors.toList());
    }

    // 상담 결과 생성
    @Transactional
    public CounselingResultDTO.Response create(CounselingResultDTO.CreateRequest request) {
        User student = userRepository.findById(request.getStudentId())
                .orElseThrow(() -> new NoSuchElementException("Student not found"));

        User instructor = userRepository.findById(request.getInstructorId())
                .orElseThrow(() -> new NoSuchElementException("Instructor not found"));

        ConsultingBooking booking = null;
        if (request.getBookingId() != null) {
            booking = consultingBookingRepository.findById(request.getBookingId())
                    .orElseThrow(() -> new NoSuchElementException("Booking not found"));
        }

        CounselingResult result = CounselingResult.createCounselingResult(
                student,
                instructor,
                booking,
                request.getMd(),
                request.getCounselAt()
        );

        return CounselingResultDTO.Response.from(counselingResultRepository.save(result));
    }

    // 상담 결과 수정
    @Transactional
    public CounselingResultDTO.Response update(Long id, CounselingResultDTO.UpdateRequest request) {
        CounselingResult result = counselingResultRepository.findByIdWithUsers(id)
                .orElseThrow(() -> new NoSuchElementException("Counseling result not found"));

        result.updateContent(request.getMd());
        return CounselingResultDTO.Response.from(result);
    }

    // 상담 결과 삭제
    @Transactional
    public void delete(Long id) {
        CounselingResult result = counselingResultRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Counseling result not found"));

        result.delete();
    }
    
} 