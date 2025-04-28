package store.mtvs.academyconnect.lunchmatching.service;

import jakarta.transaction.Transactional;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import store.mtvs.academyconnect.lunchmatching.domain.entity.LunchMatching;
import store.mtvs.academyconnect.lunchmatching.domain.entity.LunchMatchingClass;
import store.mtvs.academyconnect.lunchmatching.infrastructure.repository.LunchMatchingClassRepository;
import store.mtvs.academyconnect.lunchmatching.infrastructure.repository.LunchMatchingRepository;
import store.mtvs.academyconnect.lunchmatching.infrastructure.repository.UserRepositoryImpl;
import store.mtvs.academyconnect.user.domain.entity.User;
import store.mtvs.academyconnect.lunchmatching.infrastructure.repository.UserRepository;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Service
public class LunchMatchingService {

    private final LunchMatchingRepository lunchMatchingRepository;
    private final LunchMatchingClassRepository lunchMatchingClassRepository;
    private final UserRepository userRepository;

    // 시간 제한 메시지 상수 선언
    private static final String APPLY_RESTRICTED = "현재는 점심 신청이 제한된 시간입니다. (11:30~13:00)";
    private static final String CANCEL_RESTRICTED = "현재는 점심 신청 취소가 제한된 시간입니다. (11:30~13:00)";

    // 생성자를 통해 필요한 레포지토리 주입
    public LunchMatchingService(LunchMatchingRepository lunchMatchingRepository,LunchMatchingClassRepository lunchMatchingClassRepository){
        this.lunchMatchingRepository = lunchMatchingRepository;
        this.lunchMatchingClassRepository = lunchMatchingClassRepository;
        userRepository = new UserRepositoryImpl();
    }

    /**
     * 점심 매칭 신청 처리 메서드
     * @param userId 신청자 ID
     * @param lunchMatchingClassId 신청하려는 매칭 클래스 ID
     */
    @Transactional
    public void apply(String userId, Long lunchMatchingClassId) {

        // 11:30~13:00 시간대에는 신청 제한
        if (isRestrictedTime()) {
            throw new IllegalArgumentException("현재는 점심 신청이 제한된 시간입니다. (11:30~13:00)");
        }

        // 사용자 정보 조회 (없으면 예외 발생)
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자 없음"));

        // 수강생(STUDENT)만 신청 가능
        if (!"STUDENT".equalsIgnoreCase(user.getRole())) {
            throw new IllegalArgumentException("수강생만 신청할 수 있습니다.");
        }

        // 탈퇴한 사용자(DeletedAt 값이 존재)는 신청 불가
        if(user.getDeletedAt() != null) {
            throw new IllegalArgumentException("삭제된 사용자는 신청 할 수 없습니다.");
        }

        // 신청할 매칭 클래스 정보 조회 (없으면 예외)
        LunchMatchingClass lunchClass = lunchMatchingClassRepository.findById(lunchMatchingClassId)
                .orElseThrow(() -> new IllegalArgumentException("매칭 클래스가 존재하지 않습니다."));

        // 본인의 전공이 매칭 클래스 이름에 포함되어 있어야 신청 가능
        String userMajor = user.getClassGroup().getName();
        String matchName = lunchClass.getName();
        if(!matchName.contains(userMajor)) {
            throw new IllegalArgumentException("본인 전공과 관련된 매칭만 신청할 수 있습니다.");
        }

        // 해당 클래스에 신청한 인원이 6명 이상이면 신청 불가
        int currentCount = lunchMatchingRepository.countByLunchMatchingClassId(lunchMatchingClassId);
        if(currentCount >= 6) {
            throw new IllegalArgumentException("신청 인원이 초과되었습니다.");
        }

        // 해당 유저가 이미 동일한 매칭 클래스에 신청했는지 확인 (중복 신청 방지)
        if (lunchMatchingRepository.existsByUserIdAndLunchMatchingClassId(user.getId(), lunchMatchingClassId)) {
            throw new IllegalStateException("이미 신청한 매칭입니다.");
        }

        // 매칭 정보 생성 및 저장
        LunchMatching matching = LunchMatching.builder()
                .id(UUID.randomUUID().toString()) // 고유 식별자 생성
                .user(user) // 신청자 정보
                .lunchMatchingClass(lunchClass) // 신청한 클래스
                .createdAt(LocalDateTime.now()) // 신청 시간
                .build();

        lunchMatchingRepository.save(matching); // 매칭 정보 저장
    }

    /**
     * 점심 매칭 신청 취소 처리 (Soft Delete 방식)
     */
    @Transactional
    public void cancel(String userId, Long lunchMatchingClassId) {
        LunchMatching matching = lunchMatchingRepository
                .findByUserIdAndLunchMatchingClassIdAndDeletedAtIsNull(userId, lunchMatchingClassId)
                .orElseThrow(() -> new IllegalArgumentException("신청 내역이 없습니다."));

        matching.setDeletedAt(LocalDateTime.now());
    }

    /**
     * 11:30 ~ 12:59 사이인지 확인하는 유틸 메서드
     * 이 시간대에는 신청 및 취소가 제한됨
     *
     * @return true: 제한 시간대 / false: 가능 시간대
     */
    private boolean isRestrictedTime() {
        LocalTime now = LocalTime.now(); // 현재 시간
        LocalTime start = LocalTime.of(11, 30); // 시작 시간
        LocalTime end = LocalTime.of(13, 0); // 종료 시간 (13:00 미포함)

        return !now.isBefore(start) && now.isBefore(end); // 11:30 이상 && 13:00 미만
    }

    /**
     * 점심 매칭 전체 초기화 (Soft Delete 처리)
     * - 13시 리셋용
     */
    @Transactional
    public void resetAllMatchings() {
        List<LunchMatching> all = lunchMatchingRepository.findAllByDeletedAtIsNull();
        for (LunchMatching matching : all) {
            matching.setDeletedAt(LocalDateTime.now());
        }
    }

    /**
     * 매일 오후 13시에 자동으로 신청 내역 초기화
     * 운영환경용 자동 리셋 (@Scheduled 사용)
     */
    @Scheduled(cron = "0 0 13 * * *", zone = "Asia/Seoul")
    @Transactional
    public void scheduledResetAllMatchings() {
        resetAllMatchings(); // 기존 리셋 로직 재사용
    }

//    /**
//     * 테스트용 자동 리셋 (10초마다 실행)
//     * - 개발 환경에서만 사용
//     */
//    @Scheduled(cron = "*/10 * * * * *", zone = "Asia/Seoul")
//    @Transactional
//    public void scheduledResetAllMatchings() {
//        System.out.println("✅ [자동 리셋] 점심 매칭 초기화 실행됨: " + LocalDateTime.now()); // 로그 찍기
//        resetAllMatchings(); // 기존 로직 실행
//    }

}
