package store.mtvs.academyconnect.consulting.infrastructure.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import store.mtvs.academyconnect.consulting.domain.entity.ConsultingSlot;
import store.mtvs.academyconnect.user.domain.entity.User;

import java.time.LocalDateTime;
import java.util.List;

public interface ConsultingSlotRepository extends JpaRepository<ConsultingSlot, Long> {
    // 특정 강사의 상담 슬롯 조회
    List<ConsultingSlot> findByInstructor(User instructor);
    
    // 특정 시간대의 상담 슬롯 조회
    List<ConsultingSlot> findByStartTimeBetween(LocalDateTime start, LocalDateTime end);
    
    // 특정 상태의 상담 슬롯 조회
    List<ConsultingSlot> findByStatus(ConsultingSlot.SlotStatus status);
} 