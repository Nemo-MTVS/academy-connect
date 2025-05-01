package store.mtvs.academyconnect.consulting.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import store.mtvs.academyconnect.consulting.domain.entity.ConsultingBooking;
import store.mtvs.academyconnect.consulting.domain.entity.ConsultingSlot;
import store.mtvs.academyconnect.consulting.domain.entity.UndefinedConsulting;
import store.mtvs.academyconnect.consulting.dto.InstructorScheduleDto;
import store.mtvs.academyconnect.consulting.dto.UndefinedConsultingRequestDto;
import store.mtvs.academyconnect.consulting.infrastructure.repository.ConsultingBookingRepository;
import store.mtvs.academyconnect.consulting.infrastructure.repository.ConsultingSlotRepository;
import store.mtvs.academyconnect.user.domain.entity.User;
import store.mtvs.academyconnect.user.infrastructure.repository.UserRepository;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InstructorSchedulingService {

    private final ConsultingSlotRepository consultingSlotRepository;
    private final ConsultingBookingRepository consultingBookingRepository;
    private final UserRepository userRepository;
    private final Clock clock;

    /**
     * 강사의 스케줄 조회
     * @param instructorId 강사 ID
     * @return 강사의 스케줄 목록 DTO
     */
    public List<InstructorScheduleDto> getInstructorSchedule(String instructorId) {
        log.info("강사 스케줄 조회 서비스 호출: instructorId={}", instructorId);
        
        // 강사 ID 존재 여부 확인
        log.debug("강사 ID 조회 시작: {}", instructorId);
        Optional<User> instructorOpt = userRepository.findById(instructorId);
        
        // 강사가 없으면 빈 목록 반환 (예외 발생 대신)
        if (instructorOpt.isEmpty()) {
            log.warn("강사를 찾을 수 없음 (빈 목록 반환): instructorId={}", instructorId);
            return Collections.emptyList();
        }
        
        User instructor = instructorOpt.get();
        log.debug("강사 조회 성공: name={}", instructor.getName());

        // ClockConfiguration을 통해 현재 시간 조회
        LocalDateTime now = LocalDateTime.now(clock);

        YearMonth currentMonth = YearMonth.of(now.getYear(), now.getMonth());
        LocalDateTime startDate = currentMonth.atDay(1).atStartOfDay();
        LocalDateTime endDate = currentMonth.plusMonths(2).atDay(1).atStartOfDay();

        List<ConsultingSlot> slots = consultingSlotRepository.findTimeSlotsByInstructor(
                instructorId, startDate, endDate
        );
        List<ConsultingBooking> bookings = consultingBookingRepository.findBookingByInstructorAndTime(
                instructorId, startDate, endDate
        );

        Map<String, ConsultingBooking> bookingMap = bookings.stream()
                .collect(Collectors.toMap(
                        b -> buildKey(b.getInstructor().getId(), b.getStartTime(), b.getEndTime()),
                        Function.identity()
                ));


        log.debug("전체 스케줄 조회 결과: {} 건", slots.size());
        //

        return convertToSlotDto(slots, bookingMap);
    }
    private String buildKey(String instructorId, LocalDateTime start, LocalDateTime end) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        return instructorId + "|" + start.format(formatter) + "|" + end.format(formatter);
    }




    /**
     * 슬롯 Entity를 DTO로 변환
     */
//    private List<InstructorScheduleDto> convertToSlotDto (List<ConsultingSlot> slots) {
//        return slots.stream()
//                .map(slot -> InstructorScheduleDto.builder()
//                        .id(slot.getId())
//                        .startTime(slot.getStartTime())
//                        .endTime(slot.getEndTime())
//                        .status(slot.getStatus().toString())
//                        .build())
//                .collect(Collectors.toList());
//    }
    private List<InstructorScheduleDto> convertToSlotDto(List<ConsultingSlot> slots, Map<String, ConsultingBooking> bookingMap) {
        return slots.stream()
                .map(slot -> {
                    InstructorScheduleDto.InstructorScheduleDtoBuilder builder = InstructorScheduleDto.builder()
                            .id(slot.getId())
                            .startTime(slot.getStartTime())
                            .endTime(slot.getEndTime())
                            .status(slot.getStatus().toString());

                    if ("불가능".equals(slot.getStatus().toString())) {
                        String key = buildKey(slot.getInstructor().getId(), slot.getStartTime(), slot.getEndTime());
                        ConsultingBooking booking = bookingMap.get(key);
                        if (booking != null) {
                            builder.studentName(booking.getStudent().getName());
                            builder.studentClassGroup(booking.getStudent().getClassGroup().getName());
                            builder.consultingId(booking.getId());
                        }
                    }

                    return builder.build();
                })
                .collect(Collectors.toList());
    }



    /**
     * 슬롯 삭제
     * @param deleteSlotIds   삭제해야 할 슬롯 ID
     * @param instructorId  강사 ID
     */
    @Transactional
    public void deleteSlots(List<Long> deleteSlotIds, String instructorId) {
        log.info("슬롯 삭제 서비스 호출: slotIds={}", deleteSlotIds);


        // 슬롯 ID 존재 여부 먼저 확인
        for (Long slotId : deleteSlotIds) {

            log.debug("슬롯 ID 조회 시작: {}", slotId);
            Optional<ConsultingSlot> slotOpt = consultingSlotRepository.findById(slotId);

            if (slotOpt.isEmpty()) {
                log.error("슬롯을 찾을 수 없음: slotId={}", slotId);
                throw new IllegalArgumentException("슬롯 찾을 수 없습니다. ID: " + slotId);
            }

            ConsultingSlot slot = slotOpt.get();

            // 슬롯의 강사 ID와 현재 강사 ID 비교
            if (!slot.getInstructor().getId().equals(instructorId)) {
                log.error("본인의 슬롯이 아님: slotId={}, ownerId={}, requesterId={}",
                        slotId, slot.getInstructor().getId(), instructorId);
                throw new IllegalArgumentException("본인의 슬롯만 삭제할 수 있습니다.");
            }

            // 이미 예약된 슬롯인지 확인
            if (slot.getStatus() != ConsultingSlot.SlotStatus.사용가능) {
                log.error("예약된 슬롯은 삭제 대상이 아님: slotId={}, status={}",
                        slotId, slot.getStatus());
                throw new IllegalArgumentException("예약되지 않은 슬롯만 삭제할 수 있습니다.");
            }

            // ClockConfiguration을 통해 현재 시간 조회
            LocalDateTime now = LocalDateTime.now(clock);

            // 기존 slot 객체를 수정
            slot.setStatus(ConsultingSlot.SlotStatus.불가능);  // 상태 '불가능'으로 변경
            slot.setDeletedAt(now);  // 삭제 시간 추가

            // 수정된 객체를 DB에 저장
            consultingSlotRepository.save(slot);
            log.info("슬롯 삭제 완료: ID={}", slotId);
        }
    }


    /**
     * 슬롯 생성
     * @param createSlots   삭제해야 할 슬롯 ID
     * @param instructorId  강사 ID
     */
    @Transactional
    public void createSlot(List<String> createSlots, String instructorId) {
        log.info("슬롯 생성 서비스 호출: createSlots={}, instructorId={}",
                createSlots,instructorId);

        List<LocalDateTime> slotTimes = createSlots.stream()
                .map(str -> OffsetDateTime.parse(str).toLocalDateTime())
                .collect(Collectors.toList());

        // 강사 ID 존재 여부 확인
        User instructor = userRepository.findById(instructorId)
                .orElseThrow(() -> {
                    log.error("강사를 찾을 수 없음: instructorId={}", instructorId);
                    return new IllegalArgumentException("강사를 찾을 수 없습니다.");
                });

        // 현재 시간
        LocalDateTime now = LocalDateTime.now(clock);

        for (LocalDateTime slotTime : slotTimes) {
            // 미지정 상담 요청 엔티티 생성
            ConsultingSlot newslot = ConsultingSlot.builder()
                    .instructor(instructor)
                    .startTime(slotTime)
                    .endTime(slotTime.plusHours(1))
                    .createdAt(now)
                    .deletedAt(null)
                    .status(ConsultingSlot.SlotStatus.사용가능)
                    .build();

            // 저장
            ConsultingSlot savedRequest = consultingSlotRepository.save(newslot);
            log.info("미지정 상담 요청 생성 성공: id={}", savedRequest.getId());
        }


    }


}