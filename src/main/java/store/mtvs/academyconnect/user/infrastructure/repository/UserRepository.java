package store.mtvs.academyconnect.user.infrastructure.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import store.mtvs.academyconnect.user.domain.entity.User;

public interface UserRepository extends JpaRepository<User, String> {
    // 추가적인 쿼리 메소드 정의 가능
} 