package store.mtvs.academyconnect.consulting.infrastructure.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import store.mtvs.academyconnect.consulting.domain.entity.UndefinedConsulting;

public interface UndefinedConsultingRepository extends JpaRepository<UndefinedConsulting, Long> {
}
