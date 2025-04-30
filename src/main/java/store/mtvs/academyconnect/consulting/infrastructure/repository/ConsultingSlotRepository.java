package store.mtvs.academyconnect.consulting.infrastructure.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import store.mtvs.academyconnect.consulting.domain.entity.ConsultingSlot;
import store.mtvs.academyconnect.user.domain.entity.User;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface ConsultingSlotRepository extends JpaRepository<ConsultingSlot, Long> {
    // 특정 강사의 상담 슬롯 조회
    List<ConsultingSlot> findByInstructor(User instructor);

    // 특정 시간대의 상담 슬롯 조회
    List<ConsultingSlot> findByStartTimeBetween(LocalDateTime start, LocalDateTime end);

    // 특정 상태의 상담 슬롯 조회
    List<ConsultingSlot> findByStatus(ConsultingSlot.SlotStatus status);

    // 특정 강사의 특정 날짜 범위의 사용 가능한 슬롯 조회 (S03 구현용)
    @Query("SELECT cs FROM ConsultingSlot cs WHERE cs.instructor.id = :instructorId " +
            "AND cs.startTime >= :startOfDay AND cs.startTime < :startOfNextDay " + // 시간 범위로 비교
            "AND cs.status = '사용가능' AND cs.deletedAt IS NULL " +
            "ORDER BY cs.startTime ASC")
    // 시간 순 정렬 추가
    List<ConsultingSlot> findAvailableSlotsByInstructorAndDayRange(
            @Param("instructorId") String instructorId,
            @Param("startOfDay") LocalDateTime startOfDay,         // 해당 날짜의 시작 시간 (00:00:00)
            @Param("startOfNextDay") LocalDateTime startOfNextDay); // 다음 날의 시작 시간 (00:00:00)

    // 특정 강사의 특정 년월의 사용 가능한 슬롯의 시작 시간 목록 조회
    @Query("SELECT cs FROM ConsultingSlot cs WHERE cs.instructor.id = :instructorId " +
            "AND FUNCTION('DATE', cs.startTime) = :date " + // 날짜 비교는 유지
            "AND cs.status = '사용가능' AND cs.deletedAt IS NULL")
    List<ConsultingSlot> findAvailableSlotsByInstructorAndDate(
            @Param("instructorId") String instructorId,
            @Param("date") LocalDate date);

    // 특정 강사의 특정 년월의 사용 가능한 슬롯이 있는 날짜 목록 조회 (달력 표시용)
    @Query("SELECT cs.startTime FROM ConsultingSlot cs " +
            "WHERE cs.instructor.id = :instructorId " +
            "AND YEAR(cs.startTime) = :year " +
            "AND MONTH(cs.startTime) = :month " +
            "AND cs.status = '사용가능' AND cs.deletedAt IS NULL")
    List<LocalDateTime> findAvailableStartTimesInMonthByInstructor(
            @Param("instructorId") String instructorId,
            @Param("year") int year,
            @Param("month") int month);
}