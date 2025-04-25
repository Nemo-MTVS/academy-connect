package store.mtvs.academyconnect.lunchmatching.infrastructure.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import store.mtvs.academyconnect.lunchmatching.domain.entity.LunchMatching;
import store.mtvs.academyconnect.lunchmatching.domain.entity.LunchMatchingClass;
import store.mtvs.academyconnect.user.domain.entity.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface LunchMatchingRepository extends JpaRepository<LunchMatching, String> {

    // 해당 매칭 클래스의 현재 신청 인원 수 조회
    int countByLunchMatchingClassId(Long lunchMatchingClassId);

    // 사용자가 특정 매칭 클래스에 이미 신청했는지 여부 확인 (중복 신청 방지)
    boolean existsByUserIdAndLunchMatchingClassId(String userId, Long lunchMatchingClassId);

    // 사용자의 유효한(삭제되지 않은) 신청 내역 조회 (취소 기능용)
    Optional<LunchMatching> findByUserIdAndLunchMatchingClassIdAndDeletedAtIsNull(String userId, Long lunchMatchingClassId);

    String user(User user);
}