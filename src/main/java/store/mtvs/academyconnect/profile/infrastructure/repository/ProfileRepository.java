package store.mtvs.academyconnect.profile.infrastructure.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import store.mtvs.academyconnect.profile.domain.entity.Profile;

public interface ProfileRepository extends JpaRepository<Profile, String> {
    // 추가적인 쿼리 메소드 정의 가능
} 