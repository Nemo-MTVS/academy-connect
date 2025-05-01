package store.mtvs.academyconnect.consulting.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import store.mtvs.academyconnect.consulting.domain.entity.ConsultingBooking;
import store.mtvs.academyconnect.consulting.domain.entity.ConsultingSlot;
import store.mtvs.academyconnect.consulting.dto.AvailableSlotDto;
import store.mtvs.academyconnect.consulting.dto.CalendarDayDto;
import store.mtvs.academyconnect.consulting.infrastructure.repository.ConsultingBookingRepository;
import store.mtvs.academyconnect.consulting.infrastructure.repository.ConsultingSlotRepository;

import java.time.Clock;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StudentBookingViewService {

    private final ConsultingSlotRepository consultingSlotRepository;
    private final ConsultingBookingRepository consultingBookingRepository;
    private final Clock clock;

    /**
     * 달력 데이터 생성 로직 개선
     */
    public List<List<CalendarDayDto>> getCalendarData(String instructorId, int year, int month, LocalDate selectedDate) {
        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDate firstDayOfMonth = yearMonth.atDay(1);
        LocalDate lastDayOfMonth = yearMonth.atEndOfMonth();

        // 달력 시작일: 이번 달 1일이 속한 주의 월요일 (HTML 헤더가 월요일 시작 기준)
        LocalDate startDayOfCalendar = firstDayOfMonth.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));

        // 달력 종료일: 이번 달 마지막 날이 속한 주의 일요일 (HTML 헤더가 일요일 종료 기준)
        LocalDate endDayOfCalendar = lastDayOfMonth.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));

        // --- 해당 월의 예약 가능 날짜 조회 (DB 조회) - 기존 메소드 사용 및 변환 ---
        List<LocalDateTime> availableStartTimes = consultingSlotRepository.findAvailableStartTimesInMonthByInstructor(
                instructorId, year, month);

        // 현재 시간 가져오기
        LocalDateTime now = LocalDateTime.now(clock);
        LocalDate today = now.toLocalDate();

        // 날짜별로 그룹화하고 각 날짜마다 미래 시간 슬롯이 있는지 확인
        Map<LocalDate, List<LocalDateTime>> timesByDate = availableStartTimes.stream()
                .collect(Collectors.groupingBy(LocalDateTime::toLocalDate));

        // 실제 예약 가능한 날짜만 필터링 (과거 날짜 제외 + 오늘은 미래 시간만)
        Set<LocalDate> availableDateSet = timesByDate.entrySet().stream()
                .filter(entry -> {
                    LocalDate date = entry.getKey();
                    if (date.isBefore(today)) {
                        return false; // 과거 날짜는 제외
                    }
                    if (date.equals(today)) {
                        // 오늘인 경우, 현재 시간 이후의 슬롯이 있는지 확인
                        return entry.getValue().stream()
                                .anyMatch(time -> time.isAfter(now));
                    }
                    return true; // 미래 날짜는 모두 포함
                })
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());

        // 디버그 로깅 추가
        log.debug("조회된 예약 가능 날짜 ({}년 {}월, 강사 ID: {}): {}", year, month, instructorId, availableDateSet);
        log.debug("현재 시간 필터링 후 실제 예약 가능 날짜: {}", availableDateSet);

        // --- 데이터 조회 끝 ---

        List<List<CalendarDayDto>> calendarWeeks = new ArrayList<>();
        LocalDate currentDay = startDayOfCalendar;
        List<CalendarDayDto> currentWeek = new ArrayList<>();

        // 계산된 시작일부터 종료일까지 하루씩 증가하며 처리
        while (!currentDay.isAfter(endDayOfCalendar)) {
            boolean isInMonth = currentDay.getMonthValue() == month;
            boolean isPast = currentDay.isBefore(today);
            boolean isToday = currentDay.equals(today);
            boolean isSelected = selectedDate != null && currentDay.equals(selectedDate);
            // 실제 예약 가능 여부는 미래 날짜이고, availableDates에 포함되어야 함
            boolean isActuallyAvailable = availableDateSet.contains(currentDay) && !isPast;
            // 주말 여부 (월요일=1, 일요일=7)
            DayOfWeek dayOfWeek = currentDay.getDayOfWeek();
            boolean isWeekend = dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY;

            CalendarDayDto dayDto = CalendarDayDto.builder()
                    .dayOfMonth(currentDay.getDayOfMonth())
                    .dateString(currentDay.format(DateTimeFormatter.ISO_DATE))
                    .isEmpty(!isInMonth) // 현재 달에 속하지 않으면 empty (CSS에서 숨김 처리 가능)
                    .isPast(isPast)
                    .isToday(isToday)
                    .isSelected(isSelected)
                    .isAvailable(isActuallyAvailable) // 실제 예약 가능한지 여부
                    .isWeekend(isWeekend)
                    .build();

            currentWeek.add(dayDto);

            // 현재 날짜가 일요일이거나, 달력의 마지막 날이면 한 주(week)를 완성하고 리스트에 추가
            if (dayOfWeek == DayOfWeek.SUNDAY || currentDay.equals(endDayOfCalendar)) {
                calendarWeeks.add(currentWeek);
                currentWeek = new ArrayList<>(); // 다음 주를 위해 리스트 초기화
            }

            currentDay = currentDay.plusDays(1); // 다음 날짜로 이동
        }

        log.debug("생성된 달력 데이터: {} 주", calendarWeeks.size());
        return calendarWeeks;
    }

    /**
     * 예약 가능한 시간 슬롯 목록 조회 (S03 시간 슬롯 부분) - Clock 사용, Repository 메소드 호출 수정
     */
    public List<AvailableSlotDto> getAvailableSlots(String instructorId, LocalDate date) {
        if (date == null) {
            log.warn("getAvailableSlots 호출 시 date 파라미터가 null입니다.");
            return Collections.emptyList();
        }

        // 날짜 범위를 계산 (해당 날짜 00:00:00 ~ 다음날 00:00:00 이전)
        LocalDateTime startOfDay = date.atStartOfDay(); // 예: 2025-05-07T00:00:00
        LocalDateTime startOfNextDay = date.plusDays(1).atStartOfDay(); // 예: 2025-05-08T00:00:00

        // 현재 시간 가져오기
        LocalDateTime now = LocalDateTime.now(clock);

        log.debug("현재 시간: {}, 조회 날짜: {}", now, date);

        // 날짜 범위 사용 조회로 수정한 Repository 메소드 호출
        List<ConsultingSlot> potentialSlots = consultingSlotRepository
                .findAvailableSlotsByInstructorAndDayRange(instructorId, startOfDay, startOfNextDay);

        log.debug("날짜 [{}] 강사 [{}]의 사용가능 슬롯 조회 결과: {} 개", date, instructorId, potentialSlots.size());

        // 각 슬롯 상세 정보 로깅
        for (ConsultingSlot slot : potentialSlots) {
            log.debug("슬롯 ID: {}, 시간: {} ~ {}, 상태: {}",
                    slot.getId(), slot.getStartTime(), slot.getEndTime(), slot.getStatus());
        }

        // 해당 날짜에 이미 예약된 상담들 조회
        List<ConsultingBooking> existingBookings = consultingBookingRepository
                .findBookedOrCompletedByInstructorAndDate(instructorId, date);

        log.debug("날짜 [{}] 강사 [{}]의 기존 예약 조회 결과: {} 개", date, instructorId, existingBookings.size());

        // 예약된 시간 범위들 추출
        Set<TimeRange> bookedTimeRanges = existingBookings.stream()
                .map(booking -> new TimeRange(booking.getStartTime(), booking.getEndTime()))
                .collect(Collectors.toSet());

        // 예약 가능한 슬롯만 필터링
        List<AvailableSlotDto> resultSlots = potentialSlots.stream()
                .filter(slot -> {
                    // 1. 슬롯이 '사용가능' 상태인지 확인
                    boolean isAvailable = slot.getStatus() == ConsultingSlot.SlotStatus.사용가능;

                    // 2. 슬롯이 이미 예약된 시간과 겹치지 않는지 확인
                    TimeRange slotRange = new TimeRange(slot.getStartTime(), slot.getEndTime());
                    boolean isOverlapping = bookedTimeRanges.stream().anyMatch(slotRange::overlaps);

                    // 3. 슬롯의 시작 시간이 현재 시간 이후인지 확인
                    boolean isFuture = slot.getStartTime().isAfter(now);

                    // 상세 로깅
                    log.debug("슬롯 ID: {}, 시간: {} ~ {}, 상태: {}, 사용가능: {}, 예약겹침: {}, 미래시간: {}, 최종결과: {}",
                            slot.getId(), slot.getStartTime(), slot.getEndTime(), slot.getStatus(),
                            isAvailable, isOverlapping, isFuture, (isAvailable && !isOverlapping && isFuture));

                    // 세 조건 모두 만족해야 함
                    return isAvailable && !isOverlapping && isFuture;
                })
                .map(slot -> AvailableSlotDto.builder()
                        .slotId(slot.getId())
                        .startTime(slot.getStartTime())
                        .endTime(slot.getEndTime())
                        .isSelected(false)
                        .build())
                .sorted(Comparator.comparing(AvailableSlotDto::getStartTime))
                .collect(Collectors.toList());

        log.debug("날짜 [{}] 강사 [{}]의 최종 예약 가능 슬롯: {} 개", date, instructorId, resultSlots.size());
        return resultSlots;
    }

    // TimeRange 내부 클래스
    private static class TimeRange {
        private final LocalDateTime start;
        private final LocalDateTime end;

        public TimeRange(LocalDateTime start, LocalDateTime end) {
            this.start = start;
            this.end = end;
        }

        /**
         * 시간 겹침 로직 변경:
         * 서로의 시작 시간과 종료 시간을 비교하여 실제로 시간이 겹치는 경우만 true 반환
         * (내 시작시간 < 다른 끝시간 AND 다른 시작시간 < 내 끝시간)
         */
        public boolean overlaps(TimeRange other) {
            return this.start.isBefore(other.end) && other.start.isBefore(this.end);
        }

        // 디버깅용 toString
        @Override
        public String toString() {
            return "[" + start + " - " + end + "]";
        }
    }
}