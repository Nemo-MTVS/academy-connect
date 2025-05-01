package store.mtvs.academyconnect.consulting.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import store.mtvs.academyconnect.consulting.domain.entity.ConsultingSlot;
import store.mtvs.academyconnect.consulting.domain.entity.ConsultingSlot;
import store.mtvs.academyconnect.consulting.dto.InstructorBookingDto;
import store.mtvs.academyconnect.consulting.dto.InstructorBookingListItemDto;
import store.mtvs.academyconnect.consulting.dto.MyBookingListItemDto;
import store.mtvs.academyconnect.consulting.domain.entity.ConsultingBooking;
import store.mtvs.academyconnect.consulting.dto.StudentBookingRequestDto;
import store.mtvs.academyconnect.consulting.infrastructure.repository.ConsultingBookingRepository;
import store.mtvs.academyconnect.consulting.infrastructure.repository.ConsultingSlotRepository;
import store.mtvs.academyconnect.global.config.ClockConfiguration;
import store.mtvs.academyconnect.user.domain.entity.User;
import store.mtvs.academyconnect.user.infrastructure.repository.UserRepository;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ConsultingBookingService {

    private final ConsultingSlotRepository slotRepository;
    private final ConsultingBookingRepository bookingRepository;
    private final ConsultingBookingRepository consultingBookingRepository;
    private final ConsultingSlotRepository consultingSlotRepository;
    private final UserRepository userRepository;
    private final Clock clock; // 시스템 시계 주입

    /**
     * 학생의 예정된 예약 목록 조회
     *
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
     *
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
     *
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
        booking.setStatus(ConsultingBooking.BookingStatus.취소됨);
        booking.setUpdateAt(now);
        consultingBookingRepository.save(booking);

        // 추가: 해당 시간의 강사 슬롯 상태를 '사용가능'으로 변경
        log.debug("해당 시간의 강사 슬롯 조회 시작: instructorId={}, startTime={}, endTime={}",
                booking.getInstructor().getId(), booking.getStartTime(), booking.getEndTime());

        List<ConsultingSlot> slots = consultingSlotRepository.findByInstructorAndStartTimeAndEndTime(
                booking.getInstructor(),
                booking.getStartTime(),
                booking.getEndTime()
        );

        if (slots.isEmpty()) {
            log.warn("해당 시간의 강사 슬롯을 찾을 수 없음: instructorId={}, startTime={}, endTime={}",
                    booking.getInstructor().getId(), booking.getStartTime(), booking.getEndTime());
        } else {
            ConsultingSlot slot = slots.get(0);
            log.debug("강사 슬롯 조회 성공: slotId={}, 현재상태={}", slot.getId(), slot.getStatus());

            if (slot.getStatus() == ConsultingSlot.SlotStatus.불가능) {
                slot.setStatus(ConsultingSlot.SlotStatus.사용가능);
                consultingSlotRepository.save(slot);
                log.info("강사 슬롯 상태 변경 성공: slotId={}, 변경상태=사용가능", slot.getId());
            } else {
                log.warn("강사 슬롯이 이미 사용가능 상태임: slotId={}, status={}",
                        slot.getId(), slot.getStatus());
            }
        }

        log.info("예약 취소 성공: bookingId={}", bookingId);
    }

    @Transactional
    public ConsultingBooking createBookingFromSlot(String studentId, StudentBookingRequestDto requestDto) {
        log.info("시간 지정 예약 생성 시도: studentId={}, instructorId={}, slotId={}",
                studentId, requestDto.getInstructorId(), requestDto.getSlotId());

        try {
            // 선택된 슬롯 가져오기
            log.debug("1. 선택된 슬롯 조회 시도: slotId={}", requestDto.getSlotId());
            ConsultingSlot slot = slotRepository.findById(requestDto.getSlotId())
                    .orElseThrow(() -> new IllegalArgumentException("선택한 슬롯이 존재하지 않습니다."));
            log.debug("1-1. 슬롯 조회 성공: slotId={}, status={}, instructorId={}, startTime={}",
                    slot.getId(), slot.getStatus(), slot.getInstructor().getId(), slot.getStartTime());

            // 슬롯 정보로 변수 초기화
            LocalDateTime requestedStartTime = slot.getStartTime();
            LocalDateTime requestedEndTime = slot.getEndTime();
            String instructorId = slot.getInstructor().getId();

            // 0. 현재 시간 이후인지 확인
            log.debug("0. 과거 시간 체크: requestedTime={}, currentTime={}",
                    requestedStartTime, LocalDateTime.now());
            if (requestedStartTime.isBefore(LocalDateTime.now())) {
                throw new IllegalArgumentException("과거 시간으로는 예약할 수 없습니다.");
            }

            // 1. 슬롯 가용성 확인
            log.debug("2. 슬롯 가용성 확인: status={}", slot.getStatus());
            if (slot.getStatus() != ConsultingSlot.SlotStatus.사용가능) {
                throw new IllegalArgumentException("이미 예약되었거나 사용할 수 없는 슬롯입니다.");
            }

            // 2. 최종 중복 예약 확인
            log.debug("3. 중복 예약 확인 시도: instructorId={}, startTime={}",
                    instructorId, requestedStartTime);
            boolean alreadyBooked = bookingRepository.existsByInstructor_IdAndStartTimeAndStatusIn(
                    instructorId,
                    requestedStartTime,
                    Arrays.asList(ConsultingBooking.BookingStatus.예약됨, ConsultingBooking.BookingStatus.상담완료)
            );
            log.debug("3-1. 중복 예약 확인 결과: alreadyBooked={}", alreadyBooked);

            if (alreadyBooked) {
                throw new IllegalStateException("해당 시간은 다른 사용자가 방금 예약했습니다.");
            }

            // 3. ConsultingSlot 상태 변경
            log.debug("4. 슬롯 상태 변경 시도: slotId={}, 이전상태={}",
                    slot.getId(), slot.getStatus());
            slot.setStatus(ConsultingSlot.SlotStatus.불가능);
            slotRepository.save(slot);
            log.debug("4-1. 슬롯 상태 변경 완료: 새상태={}", slot.getStatus());

            // 4. 사용자(학생, 강사) 조회
            log.debug("5. 사용자 정보 조회 시도: studentId={}, instructorId={}",
                    studentId, instructorId);
            User student = userRepository.findById(studentId)
                    .orElseThrow(() -> new IllegalArgumentException("학생 정보를 찾을 수 없습니다."));

            User instructor = userRepository.findById(instructorId)
                    .orElseThrow(() -> new IllegalArgumentException("강사 정보를 찾을 수 없습니다."));
            log.debug("5-1. 사용자 정보 조회 완료: student={}, instructor={}",
                    student.getName(), instructor.getName());

            // 5. 예약 레코드 생성 및 저장
            log.debug("6. 새 예약 엔티티 생성 시도");
            ConsultingBooking newBooking = ConsultingBooking.builder()
                    .student(student)
                    .instructor(instructor)
                    .startTime(requestedStartTime)
                    .endTime(requestedEndTime)
                    .status(ConsultingBooking.BookingStatus.예약됨)
                    .message(requestDto.getMessage() != null ? requestDto.getMessage() : "")
                    .createdAt(LocalDateTime.now())
                    .updateAt(LocalDateTime.now())
                    .build();
            log.debug("6-1. 예약 엔티티 생성 완료");

            log.debug("7. 예약 정보 저장 시도 (save 호출 직전)");
            ConsultingBooking savedBooking = bookingRepository.save(newBooking);
            log.debug("7-1. 예약 정보 저장 완료 (save 호출 직후): bookingId={}",
                    savedBooking != null ? savedBooking.getId() : "null");

            log.info("시간 지정 예약 생성 완료: bookingId={}",
                    savedBooking != null ? savedBooking.getId() : "null");
            return savedBooking;
        } catch (Exception e) {
            log.error("예약 생성 중 오류 발생", e);
            throw e;
        }
    }



    // 강사 기능 구현

    /**
     * 강사의 예정된 예약 목록 조회
     *
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
                .sorted(Comparator.comparing(ConsultingBooking::getStartTime))
                .collect(Collectors.toList());

        log.info("예정된 예약 필터링 결과: {} 건", upcomingBookings.size());
        return convertToDtoIns(upcomingBookings);

    }


    /**
     * 강사의 지난 예약 목록 조회
     *
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
        log.debug("강사 조회 성공: name={}", instructor.getName());

        List<ConsultingBooking> bookings = consultingBookingRepository.findByInstructor(instructor);
        log.debug("전체 예약 조회 결과: {} 건", bookings.size());

        // ClockConfiguration을 통해 현재 시간 조회
        LocalDateTime now = LocalDateTime.now(clock);

        List<ConsultingBooking> waiting = bookings.stream()
                .filter(booking -> booking.getStatus() == ConsultingBooking.BookingStatus.예약됨 &&
                        booking.getStartTime().isBefore(now)) // 지난 예약 중 예약됨
                .sorted(Comparator.comparing(ConsultingBooking::getStartTime).reversed())
                .collect(Collectors.toList());

        List<ConsultingBooking> complete = bookings.stream()
                .filter(booking ->
                        booking.getStatus() == ConsultingBooking.BookingStatus.상담완료 ||
                                booking.getStatus() == ConsultingBooking.BookingStatus.취소됨)
                .sorted(Comparator.comparing(ConsultingBooking::getStartTime).reversed())
                .collect(Collectors.toList());

// 두 리스트 이어붙이기
        List<ConsultingBooking> pastBookings = new ArrayList<>();
        pastBookings.addAll(waiting);
        pastBookings.addAll(complete);

        log.info("정렬된 지난 예약 결과: {} 건", pastBookings.size());

        return convertToDtoIns(pastBookings);

    }

    /**
     * 예약 Entity를 DTO로 변환
     */
    private List<InstructorBookingListItemDto> convertToDtoIns(List<ConsultingBooking> bookings) {
        return bookings.stream()
                .map(booking -> InstructorBookingListItemDto.builder()
                        .id(booking.getId())
                        .studentName(booking.getStudent().getName())
                        .classGroup(booking.getStudent().getClassGroup().getName())
                        .filePath(booking.getStudent().getProfile().getFilePath())
                        .status(booking.getStatus().toString())
                        .message(booking.getMessage()) // message는 null일 수 있음
                        .startTime(booking.getStartTime())
                        .createdAt(booking.getCreatedAt())
                        .build())
                .collect(Collectors.toList());
    }

    /**
     * 예약 취소
     *
     * @param bookingId    예약 ID
     * @param instructorId 강사 ID
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

        // 서비스 계층에서 직접 상태 변경
        ConsultingBooking updatedBooking = ConsultingBooking.builder()
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
        Optional<ConsultingSlot> slotOpt = consultingSlotRepository
                .findByInstructorIdAndStartTimeAndEndTime(
                        booking.getInstructor().getId(),
                        booking.getStartTime(),
                        booking.getEndTime()
                );

        if (slotOpt.isPresent()) {
            ConsultingSlot slot = slotOpt.get();
            slot.setStatus(ConsultingSlot.SlotStatus.사용가능); // enum 값에 따라 이름 조정
            consultingSlotRepository.save(slot);
            log.info("슬롯 상태를 '사용가능'으로 변경: slotId={}", slot.getId());
        } else {
            log.warn("해당 슬롯을 찾을 수 없음: instructorId={}, start={}, end={}",
                    booking.getInstructor().getId(), booking.getStartTime(), booking.getEndTime());
        }

        log.info("예약 취소 성공: bookingId={}", bookingId);
    }




    /**
     * 예약 처리
     *
     * @param bookingId    예약 ID
     * @param instructorId 강사 ID
     */
    @Transactional
    public void confirmInstructorBooking(Long bookingId, String instructorId) {
        log.info("예약 처리 서비스 호출: bookingId={}, instructorId={}", bookingId, instructorId);

        // 강사 ID 존재 여부 확인
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
            throw new IllegalArgumentException("본인의 예약만 처리할 수 있습니다.");
        }

        // 이미 취소되었거나 완료된 예약인지 확인
        if (booking.getStatus() != ConsultingBooking.BookingStatus.예약됨) {
            log.error("예약 상태가 취소 가능한 상태가 아님: bookingId={}, status={}",
                    bookingId, booking.getStatus());
            throw new IllegalArgumentException("예약된 상태의 상담만 취소할 수 있습니다.");
        }

        // ClockConfiguration을 통해 현재 시간 조회
        LocalDateTime now = LocalDateTime.now(clock);

        // 예약 시작 시간이 현재보다 이후인지 확인
        if (booking.getStartTime().isAfter(now)) {
            log.error("아직 시간이 지나지 않은 상담: bookingId={}, startTime={}, now={}",
                    bookingId, booking.getStartTime(), now);
            throw new IllegalArgumentException("시간이 지나지 않은 상담은 처리할 수 없습니다.");
        }

        // 서비스 계층에서 직접 상태 변경
        ConsultingBooking updatedBooking = ConsultingBooking.builder()
                .student(booking.getStudent())
                .instructor(booking.getInstructor())
                .status(ConsultingBooking.BookingStatus.상담완료)
                .message(booking.getMessage())
                .createdAt(booking.getCreatedAt())
                .updateAt(now)
                .startTime(booking.getStartTime())
                .endTime(booking.getEndTime())
                .build();

        consultingBookingRepository.save(updatedBooking);
        log.info("상담 처리 성공: bookingId={}", bookingId);
    }

    /**
     * 강사 예약 생성
     *
     * @param instructorId    강사 ID
     * @param requestDto   예약 요청 DTO
     */
    @Transactional
    public ConsultingBooking createBookingFromSchedule(String instructorId, InstructorBookingDto requestDto) {
        log.info("강사 시간 지정 예약 생성 시도: instructorId={}, instructorId={}, startTime={}",
                instructorId, requestDto.getStudentId(), requestDto.getStartTime());

        try {
            // 선택된 슬롯 가져오기
            log.debug("1. 선택된 슬롯 조회 시도: slotId={}", requestDto.getSlotId());
            ConsultingSlot slot = consultingSlotRepository.findById(requestDto.getSlotId())
                    .orElseThrow(() -> new IllegalArgumentException("선택한 슬롯이 존재하지 않습니다."));
            log.debug("1-1. 슬롯 조회 성공: slotId={}, status={}, instructorId={}, startTime={}",
                    slot.getId(), slot.getStatus(), slot.getInstructor().getId(), slot.getStartTime());

            // 슬롯 정보로 변수 초기화
            LocalDateTime requestedStartTime = slot.getStartTime();
            LocalDateTime requestedEndTime = slot.getEndTime();
            String studentId = requestDto.getStudentId();


            // 0. 현재 시간 이후인지 확인
            log.debug("0. 과거 시간 체크: requestedTime={}, currentTime={}",
                    requestedStartTime, LocalDateTime.now());
            if (requestedStartTime.isBefore(LocalDateTime.now())) {
                throw new IllegalArgumentException("과거 시간으로는 예약할 수 없습니다.");
            }

            // 1. 슬롯 가용성 확인
            log.debug("2. 슬롯 가용성 확인: status={}", slot.getStatus());
            if (slot.getStatus() != ConsultingSlot.SlotStatus.사용가능) {
                throw new IllegalArgumentException("이미 예약되었거나 사용할 수 없는 슬롯입니다.");
            }

            // 2. 최종 중복 예약 확인
            log.debug("3. 중복 예약 확인 시도: instructorId={}, startTime={}",
                    instructorId, requestedStartTime);
            boolean alreadyBooked = consultingBookingRepository.existsByInstructor_IdAndStartTimeAndStatusIn(
                    instructorId,
                    requestedStartTime,
                    Arrays.asList(ConsultingBooking.BookingStatus.예약됨, ConsultingBooking.BookingStatus.상담완료)
            );
            log.debug("3-1. 중복 예약 확인 결과: alreadyBooked={}", alreadyBooked);

            if (alreadyBooked) {
                throw new IllegalStateException("해당 시간은 다른 사용자가 방금 예약했습니다.");
            }

            // 3. ConsultingSlot 상태 변경
            log.debug("4. 슬롯 상태 변경 시도: slotId={}, 이전상태={}",
                    slot.getId(), slot.getStatus());
            slot.setStatus(ConsultingSlot.SlotStatus.불가능);
            consultingSlotRepository.save(slot);
            log.debug("4-1. 슬롯 상태 변경 완료: 새상태={}", slot.getStatus());

            // 4. 사용자(학생, 강사) 조회
            log.debug("5. 사용자 정보 조회 시도: studentId={}, instructorId={}",
                    studentId, instructorId);
            User student = userRepository.findById(studentId)
                    .orElseThrow(() -> new IllegalArgumentException("학생 정보를 찾을 수 없습니다."));

            User instructor = userRepository.findById(instructorId)
                    .orElseThrow(() -> new IllegalArgumentException("강사 정보를 찾을 수 없습니다."));
            log.debug("5-1. 사용자 정보 조회 완료: student={}, instructor={}",
                    student.getName(), instructor.getName());

            // 5. 예약 레코드 생성 및 저장
            log.debug("6. 새 예약 엔티티 생성 시도");
            ConsultingBooking newBooking = ConsultingBooking.builder()
                    .student(student)
                    .instructor(instructor)
                    .startTime(requestedStartTime)
                    .endTime(requestedEndTime)
                    .status(ConsultingBooking.BookingStatus.예약됨)
                    .message(null)
                    .createdAt(LocalDateTime.now())
                    .updateAt(LocalDateTime.now())
                    .build();
            log.debug("6-1. 예약 엔티티 생성 완료");

            log.debug("7. 예약 정보 저장 시도 (save 호출 직전)");
            ConsultingBooking savedBooking = consultingBookingRepository.save(newBooking);
            log.debug("7-1. 예약 정보 저장 완료 (save 호출 직후): bookingId={}",
                    savedBooking != null ? savedBooking.getId() : "null");

            log.info("시간 지정 예약 생성 완료: bookingId={}",
                    savedBooking != null ? savedBooking.getId() : "null");
            return savedBooking;
        } catch (Exception e) {
            log.error("예약 생성 중 오류 발생", e);
            throw e;
        }
    }
}