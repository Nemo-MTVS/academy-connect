package store.mtvs.academyconnect.consulting.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import store.mtvs.academyconnect.consulting.dto.InstructorBookingListItemDto;
import store.mtvs.academyconnect.consulting.dto.MyBookingListItemDto;
import store.mtvs.academyconnect.consulting.domain.entity.ConsultingBooking;
import store.mtvs.academyconnect.consulting.infrastructure.repository.ConsultingBookingRepository;
import store.mtvs.academyconnect.global.config.ClockConfiguration;
import store.mtvs.academyconnect.user.domain.entity.User;
import store.mtvs.academyconnect.user.infrastructure.repository.UserRepository;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ConsultingBookingService {

    private final ConsultingBookingRepository consultingBookingRepository;
    private final UserRepository userRepository;
    private final Clock clock; // 시스템 시계 주입

    /**
     * 학생의 예정된 예약 목록 조회
     * @param studentId 학생 ID
     * @return 예약 목록 DTO
     */
    public List<MyBookingListItemDto> getUpcomingBookings(String studentId) {
        log.info("예정된 예약 목록 조회 서비스 호출: studentId={}", studentId);
        
        // 학생 ID 존재 여부 확인
        log.debug("학생 ID 조회 시작: {}", studentId);
        Optional<User> studentOpt = userRepository.findById(studentId);
        
        // 학생이 없으면 빈 목록 반환 (예외 발생 대신)
        if (studentOpt.isEmpty()) {
            log.warn("학생을 찾을 수 없음 (빈 목록 반환): studentId={}", studentId);
            return Collections.emptyList();
        }
        
        User student = studentOpt.get();
        log.debug("학생 조회 성공: name={}", student.getName());
        
        List<ConsultingBooking> bookings = consultingBookingRepository.findByStudent(student);
        log.debug("전체 예약 조회 결과: {} 건", bookings.size());
        
        // ClockConfiguration을 통해 현재 시간 조회
        LocalDateTime now = LocalDateTime.now(clock);
        
        // 예정된 예약은 현재 시간 이후의 예약 중 '예약됨' 상태인 것
        List<ConsultingBooking> upcomingBookings = bookings.stream()
                .filter(booking -> booking.getStartTime().isAfter(now) && 
                                  booking.getStatus() == ConsultingBooking.BookingStatus.예약됨)
                .collect(Collectors.toList());
        
        log.info("예정된 예약 필터링 결과: {} 건", upcomingBookings.size());
        return convertToDto(upcomingBookings);
    }

    /**
     * 학생의 지난 예약 목록 조회
     * @param studentId 학생 ID
     * @return 예약 목록 DTO
     */
    public List<MyBookingListItemDto> getPastBookings(String studentId) {
        log.info("지난 예약 목록 조회 서비스 호출: studentId={}", studentId);
        
        // 학생 ID 존재 여부 확인
        log.debug("학생 ID 조회 시작: {}", studentId);
        Optional<User> studentOpt = userRepository.findById(studentId);
        
        // 학생이 없으면 빈 목록 반환 (예외 발생 대신)
        if (studentOpt.isEmpty()) {
            log.warn("학생을 찾을 수 없음 (빈 목록 반환): studentId={}", studentId);
            return Collections.emptyList();
        }
        
        User student = studentOpt.get();
        log.debug("학생 조회 성공: name={}", student.getName());
        
        List<ConsultingBooking> bookings = consultingBookingRepository.findByStudent(student);
        log.debug("전체 예약 조회 결과: {} 건", bookings.size());
        
        // ClockConfiguration을 통해 현재 시간 조회
        LocalDateTime now = LocalDateTime.now(clock);
        
        // 지난 예약은 현재 시간 이전의 예약 또는 '상담완료'나 '취소됨' 상태인 것
        List<ConsultingBooking> pastBookings = bookings.stream()
                .filter(booking -> booking.getStartTime().isBefore(now) || 
                                  booking.getStatus() == ConsultingBooking.BookingStatus.상담완료 ||
                                  booking.getStatus() == ConsultingBooking.BookingStatus.취소됨)
                .collect(Collectors.toList());
        
        log.info("지난 예약 필터링 결과: {} 건", pastBookings.size());
        return convertToDto(pastBookings);
    }

    /**
     * 예약 Entity를 DTO로 변환
     */
    private List<MyBookingListItemDto> convertToDto(List<ConsultingBooking> bookings) {
        return bookings.stream()
                .map(booking -> MyBookingListItemDto.builder()
                        .id(booking.getId())
                        .instructorName(booking.getInstructor().getName())
                        .status(booking.getStatus().toString())
                        .message(booking.getMessage()) // message는 null일 수 있음
                        .startTime(booking.getStartTime())
                        .endTime(booking.getEndTime())
                        .createdAt(booking.getCreatedAt())
                        .build())
                .collect(Collectors.toList());
    }

    /**
     * 예약 취소
     * @param bookingId 예약 ID
     * @param studentId 학생 ID
     */
    @Transactional
    public void cancelBooking(Long bookingId, String studentId) {
        log.info("예약 취소 서비스 호출: bookingId={}, studentId={}", bookingId, studentId);
        
        // 학생 ID 존재 여부 먼저 확인
        log.debug("학생 ID 조회 시작: {}", studentId);
        Optional<User> studentOpt = userRepository.findById(studentId);
        
        if (studentOpt.isEmpty()) {
            log.error("학생을 찾을 수 없음: studentId={}", studentId);
            throw new IllegalArgumentException("학생을 찾을 수 없습니다. ID: " + studentId);
        }
        
        User student = studentOpt.get();
        log.debug("학생 조회 성공: name={}", student.getName());
        
        ConsultingBooking booking = consultingBookingRepository.findById(bookingId)
                .orElseThrow(() -> {
                    log.error("예약을 찾을 수 없음: bookingId={}", bookingId);
                    return new IllegalArgumentException("예약을 찾을 수 없습니다.");
                });
        
        // 예약의 학생 ID와 현재 학생 ID 비교
        if (!booking.getStudent().getId().equals(studentId)) {
            log.error("본인의 예약이 아님: bookingId={}, ownerId={}, requesterId={}", 
                    bookingId, booking.getStudent().getId(), studentId);
            throw new IllegalArgumentException("본인의 예약만 취소할 수 있습니다.");
        }
        
        // 이미 취소되었거나 완료된 예약인지 확인
        if (booking.getStatus() != ConsultingBooking.BookingStatus.예약됨) {
            log.error("예약 상태가 취소 가능한 상태가 아님: bookingId={}, status={}", 
                    bookingId, booking.getStatus());
            throw new IllegalArgumentException("예약된 상태의 상담만 취소할 수 있습니다.");
        }
        
        // ClockConfiguration을 통해 현재 시간 조회
        LocalDateTime now = LocalDateTime.now(clock);
        
        // 예약 시작 시간이 현재보다 이전인지 확인
        if (booking.getStartTime().isBefore(now)) {
            log.error("이미 시작된 상담: bookingId={}, startTime={}, now={}", 
                    bookingId, booking.getStartTime(), now);
            throw new IllegalArgumentException("이미 시작된 상담은 취소할 수 없습니다.");
        }
        
        // 서비스 계층에서 직접 상태 변경
        ConsultingBooking updatedBooking = ConsultingBooking.builder()
                .id(booking.getId())
                .student(booking.getStudent())
                .instructor(booking.getInstructor())
                .status(ConsultingBooking.BookingStatus.취소됨)
                .message(booking.getMessage())
                .createdAt(booking.getCreatedAt())
                .updateAt(now)
                .startTime(booking.getStartTime())
                .endTime(booking.getEndTime())
                .build();
        
        consultingBookingRepository.save(updatedBooking);
        log.info("예약 취소 성공: bookingId={}", bookingId);
    }





    // 강사 기능 구현

    /**
     * 강사의 예정된 예약 목록 조회
     * @param instructorId 강사 ID
     * @return 강사 예약 목록 DTO
     */
    public List<InstructorBookingListItemDto> getInstructorUpcomingBookings(String instructorId) {
        log.info("예정된 예약 목록 조회 서비스 호출: instructorId={}", instructorId);

        // 강사 ID 존재 여부 확인
        log.debug("강사 ID 조회 시작: {}", instructorId);
        Optional<User> instructorOpt = userRepository.findById(instructorId);

        // 강사 정보 없으면 빈 목록 반환 (예외 발생 대신)
        if (instructorOpt.isEmpty()) {
            log.warn("강사 정보를 찾을 수 없음 (빈 목록 반환): instructorId={}", instructorId);
            return Collections.emptyList();
        }

        User instructor = instructorOpt.get();
        log.debug("강사 조회 성공: name={}", instructor.getName());

        List<ConsultingBooking> bookings = consultingBookingRepository.findByInstructor(instructor);
        log.debug("전체 예약 조회 결과: {} 건", bookings.size());

        // ClockConfiguration을 통해 현재 시간 조회
        LocalDateTime now = LocalDateTime.now(clock);

        // 예정된 예약은 현재 시간 이후의 예약 중 '예약됨' 상태인 것
        List<ConsultingBooking> upcomingBookings = bookings.stream()
                .filter(booking -> booking.getStartTime().isAfter(now) &&
                        booking.getStatus() == ConsultingBooking.BookingStatus.예약됨)
                .collect(Collectors.toList());

        log.info("예정된 예약 필터링 결과: {} 건", upcomingBookings.size());
        return convertToDtoIns (upcomingBookings);
    }


    /**
     * 강사의 지난 예약 목록 조회
     * @param instructorId 강사 ID
     * @return 예약 목록 DTO
     */
    public List<InstructorBookingListItemDto> getInstructorPastBookings(String instructorId) {
        log.info("지난 예약 목록 조회 서비스 호출: instructorId={}", instructorId);

        // 학생 ID 존재 여부 확인
        log.debug("강사 ID 조회 시작: {}", instructorId);
        Optional<User> instructorOpt = userRepository.findById(instructorId);

        // 학생이 없으면 빈 목록 반환 (예외 발생 대신)
        if (instructorOpt.isEmpty()) {
            log.warn("강사를 찾을 수 없음 (빈 목록 반환): instructorId={}", instructorId);
            return Collections.emptyList();
        }

        User instructor = instructorOpt.get();
        log.debug("강 조회 성공: name={}", instructor.getName());

        List<ConsultingBooking> bookings = consultingBookingRepository.findByInstructor(instructor);
        log.debug("전체 예약 조회 결과: {} 건", bookings.size());

        // ClockConfiguration을 통해 현재 시간 조회
        LocalDateTime now = LocalDateTime.now(clock);

        // 지난 예약은 현재 시간 이전의 예약 또는 '상담완료'나 '취소됨' 상태인 것
        List<ConsultingBooking> pastBookings = bookings.stream()
                .filter(booking -> booking.getStartTime().isBefore(now) ||
                        booking.getStatus() == ConsultingBooking.BookingStatus.상담완료 ||
                        booking.getStatus() == ConsultingBooking.BookingStatus.취소됨)
                .collect(Collectors.toList());

        log.info("지난 예약 필터링 결과: {} 건", pastBookings.size());
        return convertToDtoIns(pastBookings);
    }

    /**
     * 예약 Entity를 DTO로 변환
     */
    private List<InstructorBookingListItemDto> convertToDtoIns (List<ConsultingBooking> bookings) {
        return bookings.stream()
                .map(booking -> InstructorBookingListItemDto.builder()
                        .id(booking.getId())
                        .studentName(booking.getStudent().getName())
                        .classGroup(booking.getStudent().getClassGroup().getName())
                        .status(booking.getStatus().toString())
                        .message(booking.getMessage()) // message는 null일 수 있음
                        .startTime(booking.getStartTime())
                        .createdAt(booking.getCreatedAt())
                        .build())
                .collect(Collectors.toList());
    }

    /**
     * 예약 취소
     * @param bookingId 예약 ID
     * @param instructorId 학생 ID
     */
    @Transactional
    public void cancelInstructorBooking(Long bookingId, String instructorId) {
        log.info("예약 취소 서비스 호출: bookingId={}, instructorId={}", bookingId, instructorId);

        // 강사 ID 존재 여부 먼저 확인
        log.debug("강사 ID 조회 시작: {}", instructorId);
        Optional<User> instructorOpt = userRepository.findById(instructorId);

        if (instructorOpt.isEmpty()) {
            log.error("강사를 찾을 수 없음: instructorId={}", instructorId);
            throw new IllegalArgumentException("강사를 찾을 수 없습니다. ID: " + instructorId);
        }

        User instructor = instructorOpt.get();
        log.debug("강사 조회 성공: name={}", instructor.getName());

        ConsultingBooking booking = consultingBookingRepository.findById(bookingId)
                .orElseThrow(() -> {
                    log.error("예약을 찾을 수 없음: bookingId={}", bookingId);
                    return new IllegalArgumentException("예약을 찾을 수 없습니다.");
                });

        // 예약의 강사 ID와 현재 강사 ID 비교
        if (!booking.getInstructor().getId().equals(instructorId)) {
            log.error("본인의 예약이 아님: bookingId={}, ownerId={}, requesterId={}",
                    bookingId, booking.getInstructor().getId(), instructorId);
            throw new IllegalArgumentException("본인의 예약만 취소할 수 있습니다.");
        }

        // 이미 취소되었거나 완료된 예약인지 확인
        if (booking.getStatus() != ConsultingBooking.BookingStatus.예약됨) {
            log.error("예약 상태가 취소 가능한 상태가 아님: bookingId={}, status={}",
                    bookingId, booking.getStatus());
            throw new IllegalArgumentException("예약된 상태의 상담만 취소할 수 있습니다.");
        }

        // ClockConfiguration을 통해 현재 시간 조회
        LocalDateTime now = LocalDateTime.now(clock);

        // 예약 시작 시간이 현재보다 이전인지 확인
        if (booking.getStartTime().isBefore(now)) {
            log.error("이미 시작된 상담: bookingId={}, startTime={}, now={}",
                    bookingId, booking.getStartTime(), now);
            throw new IllegalArgumentException("이미 시작된 상담은 취소할 수 없습니다.");
        }

        // 서비스 계층에서 직접 상태 변경
        ConsultingBooking updatedBooking = ConsultingBooking.builder()
                .id(booking.getId())
                .student(booking.getStudent())
                .instructor(booking.getInstructor())
                .status(ConsultingBooking.BookingStatus.취소됨)
                .message(booking.getMessage())
                .createdAt(booking.getCreatedAt())
                .updateAt(now)
                .startTime(booking.getStartTime())
                .endTime(booking.getEndTime())
                .build();

        consultingBookingRepository.save(updatedBooking);
        log.info("예약 취소 성공: bookingId={}", bookingId);
    }
}