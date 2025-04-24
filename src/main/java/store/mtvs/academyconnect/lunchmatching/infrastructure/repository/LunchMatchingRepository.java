package store.mtvs.academyconnect.lunchmatching.infrastructure.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import store.mtvs.academyconnect.lunchmatching.domain.entity.LunchMatching;
import store.mtvs.academyconnect.lunchmatching.domain.entity.LunchMatchingClass;
import store.mtvs.academyconnect.user.domain.entity.User;

import java.time.LocalDateTime;
import java.util.List;

public interface LunchMatchingRepository extends JpaRepository<LunchMatching, String> {

} 