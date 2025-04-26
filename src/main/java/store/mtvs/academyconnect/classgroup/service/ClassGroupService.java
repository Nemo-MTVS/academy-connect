package store.mtvs.academyconnect.classgroup.service;

import org.springframework.stereotype.Service;
import store.mtvs.academyconnect.classgroup.domain.entity.ClassGroup;
import store.mtvs.academyconnect.classgroup.infrastructure.repository.ClassGroupRepository;

import java.time.LocalDateTime;

@Service
public class ClassGroupService {

    private final ClassGroupRepository classGroupRepository;

    public ClassGroupService(ClassGroupRepository classGroupRepository) {
        this.classGroupRepository = classGroupRepository;
    }

    // 등록
    public ClassGroup createClassGroup(LocalDateTime startTime, LocalDateTime endTime, String name) {
        ClassGroup newClassGroup = new ClassGroup(
                name,
                startTime,
                endTime
        );

        // 2. ClassGroup 저장
        return classGroupRepository.save(newClassGroup);
    }

    public ClassGroup getClassGroup(String classGroupName) {
        // 1. ClassGroup 조회
        return classGroupRepository.findByName(classGroupName)
                .orElseThrow(() -> new IllegalArgumentException("ClassGroup not found"));
    }
}
