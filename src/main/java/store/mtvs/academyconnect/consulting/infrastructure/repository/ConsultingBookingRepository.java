package store.mtvs.academyconnect.consulting.infrastructure.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import store.mtvs.academyconnect.consulting.domain.entity.ConsultingBooking;
import store.mtvs.academyconnect.user.domain.entity.User;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface ConsultingBookingRepository extends JpaRepository<ConsultingBooking, Long> {
    // 학생별 상담 예약 조회
    List<ConsultingBooking> findByStudent(User student);

    // 강사별 상담 예약 조회
    List<ConsultingBooking> findByInstructor(User instructor);

    // 상담 상태별 조회
    List<ConsultingBooking> findByStatus(ConsultingBooking.BookingStatus status);

    // 특정 기간의 상담 예약 조회
    List<ConsultingBooking> findByStartTimeBetween(LocalDateTime start, LocalDateTime end);

    // 특정 강사의 특정 날짜 범위의 예약된/완료된 상담 조회 (S03 구현용)
    @Query("SELECT cb FROM ConsultingBooking cb WHERE cb.instructor.id = :instructorId " +
            "AND cb.startTime BETWEEN :startDateTime AND :endDateTime " +
            "AND cb.status IN ('예약됨', '상담완료')")
    List<ConsultingBooking> findBookedOrCompletedByInstructorAndDateRange(
            @Param("instructorId") String instructorId,
            @Param("startDateTime") LocalDateTime startDateTime,
            @Param("endDateTime") LocalDateTime endDateTime);

    // 특정 강사의 특정 날짜의 예약된/완료된 상담 조회 (S03 구현용)
    @Query("SELECT cb FROM ConsultingBooking cb WHERE cb.instructor.id = :instructorId " +
            "AND FUNCTION('DATE', cb.startTime) = :date " +
            "AND cb.status IN ('예약됨', '상담완료')")
    List<ConsultingBooking> findBookedOrCompletedByInstructorAndDate(
            @Param("instructorId") String instructorId,
            @Param("date") LocalDate date);

    // 특정 강사/년월의 예약된/완료된 상담 시작 시간 목록 조회(달력 표시용)
    @Query("SELECT cb.startTime FROM ConsultingBooking cb " +
            "WHERE cb.instructor.id = :instructorId " +
            "AND YEAR(cb.startTime) = :year " +
            "AND MONTH(cb.startTime) = :month " +
            "AND cb.status IN ('예약됨', '상담완료')")
    List<LocalDateTime> findBookedOrCompletedStartTimesInMonthByInstructor(
            @Param("instructorId") String instructorId,
            @Param("year") int year,
            @Param("month") int month);
}