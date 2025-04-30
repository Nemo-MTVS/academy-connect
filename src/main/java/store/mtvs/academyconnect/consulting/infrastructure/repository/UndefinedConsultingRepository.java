package store.mtvs.academyconnect.consulting.infrastructure.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import store.mtvs.academyconnect.consulting.domain.entity.UndefinedConsulting;
import store.mtvs.academyconnect.user.domain.entity.User;

import java.util.List;

public interface UndefinedConsultingRepository extends JpaRepository<UndefinedConsulting, Long> {

    // 학생 ID로 미지정 상담 요청 조회
    List<UndefinedConsulting> findByStudentId(String studentId);

    // 강사 ID로 미지정 상담 요청 조회
    List<UndefinedConsulting> findByInstructorId(String instructorId);

    // 상담 상태별 조회
    List<UndefinedConsulting> findByStatus(UndefinedConsulting.RequestStatus status);
}