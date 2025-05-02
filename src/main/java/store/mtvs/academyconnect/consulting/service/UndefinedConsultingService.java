package store.mtvs.academyconnect.consulting.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import store.mtvs.academyconnect.consulting.domain.entity.ConsultingBooking;
import store.mtvs.academyconnect.consulting.domain.entity.UndefinedConsulting;
import store.mtvs.academyconnect.consulting.dto.InstructorUndefinedConsultingDto;
import store.mtvs.academyconnect.consulting.dto.UndefinedConsultingDto;
import store.mtvs.academyconnect.consulting.dto.UndefinedConsultingRequestDto;
import store.mtvs.academyconnect.consulting.infrastructure.repository.UndefinedConsultingRepository;
import store.mtvs.academyconnect.user.domain.entity.User;
import store.mtvs.academyconnect.user.infrastructure.repository.UserRepository;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UndefinedConsultingService {

    private final UndefinedConsultingRepository undefinedConsultingRepository;
    private final UserRepository userRepository;
    private final Clock clock;

    /**
     * 학생의 미지정 상담 요청 목록 조회
     * @param studentId 학생 ID
     * @return 요청 목록 DTO
     */
    public List<UndefinedConsultingDto> getConsultationRequests(String studentId) {
        log.info("미지정 상담 요청 목록 조회 서비스 호출: studentId={}", studentId);

        if (!userRepository.existsById(studentId)) {
            log.warn("학생을 찾을 수 없음 (빈 목록 반환): studentId={}", studentId);
            return Collections.emptyList();
        }

        List<UndefinedConsulting> requests = undefinedConsultingRepository.findByStudentId(studentId);
        return convertToDto(requests);
    }

    /**
     * 미지정 상담 요청 생성
     * @param requestDto 요청 DTO
     * @return 생성된 요청 ID
     */
    @Transactional
    public Long createConsultationRequest(UndefinedConsultingRequestDto requestDto) {
        log.info("===== 미지정 상담 요청 생성 서비스 시작 =====");

        try {
            String studentId = requestDto.getStudentId();
            String instructorId = requestDto.getInstructorId();

            // 입력 검증
            if (studentId == null || studentId.trim().isEmpty()) {
                throw new IllegalArgumentException("학생 ID가 비어있습니다.");
            }
            if (instructorId == null || instructorId.trim().isEmpty()) {
                throw new IllegalArgumentException("강사 ID가 비어있습니다.");
            }

            // 존재 확인만 수행
            userRepository.findById(studentId).orElseThrow(() ->
                    new IllegalArgumentException("학생을 찾을 수 없습니다: " + studentId));
            userRepository.findById(instructorId).orElseThrow(() ->
                    new IllegalArgumentException("강사를 찾을 수 없습니다: " + instructorId));

            LocalDateTime now = LocalDateTime.now(clock);

            UndefinedConsulting request = UndefinedConsulting.builder()
                    .studentId(studentId)
                    .instructorId(instructorId)
                    .requestAt(now)
                    .status(UndefinedConsulting.RequestStatus.WAITING)
                    .updatedAt(now)
                    .comment(requestDto.getMessage())
                    .build();

            UndefinedConsulting saved = undefinedConsultingRepository.save(request);
            return saved.getId();
        } catch (Exception e) {
            log.error("미지정 상담 요청 생성 중 예외 발생", e);
            throw e;
        } finally {
            log.info("===== 미지정 상담 요청 생성 서비스 종료 =====");
        }
    }

    /**
     * Entity를 DTO로 변환
     */
    private List<UndefinedConsultingDto> convertToDto(List<UndefinedConsulting> requests) {
        return requests.stream()
                .map(request -> {
                    String instructorName = userRepository.findById(request.getInstructorId())
                            .map(User::getName)
                            .orElse("알 수 없음");

                    return UndefinedConsultingDto.builder()
                            .id(request.getId())
                            .instructorName(instructorName)
                            .status(request.getStatus().name())
                            .comment(request.getComment())
                            .requestAt(request.getRequestAt())
                            .updatedAt(request.getUpdatedAt())
                            .build();
                })
                .collect(Collectors.toList());
    }


    /**
     * 강사 미지정 상담 요청 목록 조회
     * @param instructorId 강사 ID
     * @return 요청 목록 DTO
     */
    public List<InstructorUndefinedConsultingDto> getInstructorConsultationRequests(String instructorId) {
        if (!userRepository.existsById(instructorId)) {
            return Collections.emptyList();
        }

        List<UndefinedConsulting> allRequests = undefinedConsultingRepository.findByInstructorId(instructorId);

        List<UndefinedConsulting> waiting = allRequests.stream()
                .filter(r -> r.getStatus() == UndefinedConsulting.RequestStatus.WAITING)
                .sorted(Comparator.comparing(UndefinedConsulting::getRequestAt).reversed())
                .collect(Collectors.toList());

        List<UndefinedConsulting> completed = allRequests.stream()
                .filter(r -> r.getStatus() == UndefinedConsulting.RequestStatus.DONE)
                .sorted(Comparator.comparing(UndefinedConsulting::getRequestAt).reversed())
                .collect(Collectors.toList());

        List<UndefinedConsulting> sortedRequests = new ArrayList<>();
        sortedRequests.addAll(waiting);
        sortedRequests.addAll(completed);

        log.info("정렬된 미지정 상담 요청 수: {}", sortedRequests.size());

        return convertToDtoIns(sortedRequests);
    }



    /**
     * Entity를 DTO로 변환
     */
    private List<InstructorUndefinedConsultingDto> convertToDtoIns(List<UndefinedConsulting> requests) {
        return requests.stream()
                .map(request -> {
                    User student = userRepository.findById(request.getStudentId()).orElse(null);
                    String studentName = student != null ? student.getName() : "알 수 없음";
                    String studentId = student != null ? student.getId() : "알 수 없음";
                    String classGroup = student != null && student.getClassGroup() != null
                            ? student.getClassGroup().getName() : "-";
                    String filePath = student != null && student.getProfile() != null
                            ? student.getProfile().getFilePath() : "";

                    return InstructorUndefinedConsultingDto.builder()
                            .id(request.getId())
                            .studentName(studentName)
                            .studentId(studentId)
                            .classGroup(classGroup)
                            .filePath(filePath)
                            .status(request.getStatus().toString())
                            .comment(request.getComment())
                            .requestAt(request.getRequestAt())
                            .updatedAt(request.getUpdatedAt())
                            .build();
                })
                .collect(Collectors.toList());
    }
}
