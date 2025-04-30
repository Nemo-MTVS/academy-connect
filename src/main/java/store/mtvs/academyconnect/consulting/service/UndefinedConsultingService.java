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
        
        // 학생 ID 존재 여부 확인
        log.debug("학생 ID 조회 시작: {}", studentId);
        Optional<User> studentOpt = userRepository.findById(studentId);
        
        // 학생이 없으면 빈 목록 반환
        if (studentOpt.isEmpty()) {
            log.warn("학생을 찾을 수 없음 (빈 목록 반환): studentId={}", studentId);
            return Collections.emptyList();
        }
        
        User student = studentOpt.get();
        log.debug("학생 조회 성공: name={}", student.getName());
        
        List<UndefinedConsulting> requests = undefinedConsultingRepository.findByStudent(student);
        log.debug("미지정 상담 요청 조회 결과: {} 건", requests.size());
        
        return convertToDto(requests);
    }

    /**
     * 미지정 상담 요청 생성
     * @param requestDto 요청 DTO
     * @return 생성된 요청 ID
     */
    @Transactional
    public Long createConsultationRequest(UndefinedConsultingRequestDto requestDto) {
        log.info("미지정 상담 요청 생성 서비스 호출: studentId={}, instructorId={}", 
                requestDto.getStudentId(), requestDto.getInstructorId());
        
        // 학생 ID 존재 여부 확인
        User student = userRepository.findById(requestDto.getStudentId())
                .orElseThrow(() -> {
                    log.error("학생을 찾을 수 없음: studentId={}", requestDto.getStudentId());
                    return new IllegalArgumentException("학생을 찾을 수 없습니다.");
                });
        
        // 강사 ID 존재 여부 확인
        User instructor = userRepository.findById(requestDto.getInstructorId())
                .orElseThrow(() -> {
                    log.error("강사를 찾을 수 없음: instructorId={}", requestDto.getInstructorId());
                    return new IllegalArgumentException("강사를 찾을 수 없습니다.");
                });
        
        // 현재 시간
        LocalDateTime now = LocalDateTime.now(clock);
        
        // ID 생성 (실제 구현에서는, 데이터베이스 시퀀스나 자동 생성 전략을 사용해야)
        Long newId = getNextId();
        
        // 미지정 상담 요청 엔티티 생성
        UndefinedConsulting request = UndefinedConsulting.builder()
                .id(newId)
                .student(student)
                .instructor(instructor)
                .requestAt(now)
                .status(UndefinedConsulting.RequestStatus.WAITING)
                .updatedAt(now)
                .comment(requestDto.getMessage())
                .build();
        
        // 저장
        UndefinedConsulting savedRequest = undefinedConsultingRepository.save(request);
        log.info("미지정 상담 요청 생성 성공: id={}", savedRequest.getId());
        
        return savedRequest.getId();
    }

    /**
     * Entity를 DTO로 변환
     */
    private List<UndefinedConsultingDto> convertToDto(List<UndefinedConsulting> requests) {
        return requests.stream()
                .map(request -> UndefinedConsultingDto.builder()
                        .id(request.getId())
                        .instructorName(request.getInstructor().getName())
                        .status(request.getStatus().name())
                        .comment(request.getComment())
                        .requestAt(request.getRequestAt())
                        .updatedAt(request.getUpdatedAt())
                        .build())
                .collect(Collectors.toList());
    }
    
    /**
     * 다음 ID 생성 (임시 구현)
     * 실제 구현에서는 데이터베이스 시퀀스나 자동 생성 전략을 사용해야
     */
    private Long getNextId() {
        // 현재 저장된 마지막 ID + 1 반환
        return undefinedConsultingRepository.count() + 1;
    }






    /**
     * 강사 미지정 상담 요청 목록 조회
     * @param instructorId 강사 ID
     * @return 요청 목록 DTO
     */
    public List<InstructorUndefinedConsultingDto> getInstructorConsultationRequests(String instructorId) {
        log.info("미지정 상담 요청 목록 조회 서비스 호출: instructorId={}", instructorId);

        // 강사 ID 존재 여부 확인
        log.debug("강사 ID 조회 시작: {}", instructorId);
        Optional<User> instructorOpt = userRepository.findById(instructorId);

        // 학생이 없으면 빈 목록 반환
        if (instructorOpt.isEmpty()) {
            log.warn("강사를 찾을 수 없음 (빈 목록 반환): instructorId={}", instructorId);
            return Collections.emptyList();
        }

        User instructor = instructorOpt.get();
        log.debug("강사 조회 성공: name={}", instructor.getName());

        List<UndefinedConsulting> requests = undefinedConsultingRepository.findByInstructor(instructor);
        log.debug("미지정 상담 요청 조회 결과: {} 건", requests.size());

        List<UndefinedConsulting> waiting = requests.stream()
                .filter(r -> r.getStatus() == UndefinedConsulting.RequestStatus.WAITING)
                .sorted(Comparator.comparing(UndefinedConsulting::getRequestAt).reversed())
                .collect(Collectors.toList());

        List<UndefinedConsulting> completed = requests.stream()
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
                .map(request -> InstructorUndefinedConsultingDto.builder()
                        .id(request.getId())
                        .studentName(request.getStudent().getName())
                        .classGroup(request.getStudent().getClassGroup().getName())
                        .filePath(request.getStudent().getProfile().getFilePath())
                        .status(request.getStatus().toString())
                        .comment(request.getComment())
                        .requestAt(request.getRequestAt())
                        .updatedAt(request.getUpdatedAt())
                        .build())
                .collect(Collectors.toList());
    }
}
