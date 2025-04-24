package store.mtvs.academyconnect.consulting.infrastructure.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import store.mtvs.academyconnect.consulting.domain.entity.ConsultingBooking;
import store.mtvs.academyconnect.user.domain.entity.User;

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
} 