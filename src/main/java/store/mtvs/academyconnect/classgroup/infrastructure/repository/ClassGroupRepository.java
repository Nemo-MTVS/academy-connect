package store.mtvs.academyconnect.classgroup.infrastructure.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import store.mtvs.academyconnect.classgroup.domain.entity.ClassGroup;

public interface ClassGroupRepository extends JpaRepository<ClassGroup, Long> {
    // 추가적인 쿼리 메소드 정의 가능
} 