package store.mtvs.academyconnect.lunchmatching.infrastructure.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import store.mtvs.academyconnect.lunchmatching.domain.entity.LunchMatchingClass;

import java.util.Optional;

public interface LunchMatchingClassRepository extends JpaRepository<LunchMatchingClass, Long> {

}