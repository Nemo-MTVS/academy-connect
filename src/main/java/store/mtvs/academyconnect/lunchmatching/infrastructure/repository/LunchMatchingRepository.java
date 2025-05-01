package store.mtvs.academyconnect.lunchmatching.infrastructure.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import store.mtvs.academyconnect.lunchmatching.domain.entity.LunchMatching;
import store.mtvs.academyconnect.lunchmatching.domain.entity.LunchMatchingClass;
import store.mtvs.academyconnect.user.domain.entity.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface LunchMatchingRepository extends JpaRepository<LunchMatching, String> {

    // 매칭 클래스 ID별 현재 살아있는 신청 인원 수 조회
    int countByLunchMatchingClassIdAndDeletedAtIsNull(Long lunchMatchingClassId);

    // 사용자가 특정 매칭 클래스에 신청한 기록이 있는지 확인 (삭제된 신청 포함)
    boolean existsByUserIdAndLunchMatchingClassId(String userId, Long lunchMatchingClassId);

    // 사용자의 삭제되지 않은 동일 매칭 신청 존재 여부 확인 (중복 신청 방지용)
    boolean existsByUserIdAndLunchMatchingClassIdAndDeletedAtIsNull(String id, Long lunchMatchingClassId);

    // 사용자의 유효한(삭제되지 않은) 신청 내역 조회 (취소 기능용)
    Optional<LunchMatching> findByUserIdAndLunchMatchingClassIdAndDeletedAtIsNull(String userId, Long lunchMatchingClassId);

    // 삭제되지 않은 모든 신청 내역 조회
    List<LunchMatching> findAllByDeletedAtIsNull();

    // 특정 매칭 클래스 ID의 삭제되지 않은 신청 내역 조회 (신청 시간 순 createdAt ASC 정렬)
    List<LunchMatching> findByLunchMatchingClassIdAndDeletedAtIsNullOrderByCreatedAtAsc(Long classId);
}