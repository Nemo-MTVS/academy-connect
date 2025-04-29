package store.mtvs.academyconnect.counselingresult.infrastructure.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.Rollback;
import store.mtvs.academyconnect.classgroup.domain.entity.ClassGroup;
import store.mtvs.academyconnect.consulting.domain.entity.ConsultingBooking;
import store.mtvs.academyconnect.counselingresult.domain.entity.CounselingResult;
import store.mtvs.academyconnect.user.domain.entity.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Rollback(false) // Disable transaction rollback to persist data
class CounselingResultRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private CounselingResultRepository counselingResultRepository;

    private User student;
    private User instructor;
    private ConsultingBooking booking;
    private CounselingResult counselingResult;


    @BeforeEach
    void setUp() {
        // Get existing student (홍길동1 from Backend class)
        student = entityManager.find(User.class, "uuid-be-001");
        assertThat(student).isNotNull();
        assertThat(student.getName()).isEqualTo("홍길동1");

        // Get existing instructor (이상우 from Backend class)
        instructor = entityManager.find(User.class, "uuid-be-ins");
        assertThat(instructor).isNotNull();
        assertThat(instructor.getName()).isEqualTo("이상우");

        // Get existing booking (ID: 101 - completed consultation)
        booking = entityManager.find(ConsultingBooking.class, 101L);
        assertThat(booking).isNotNull();
        assertThat(booking.getStatus()).isEqualTo("상담완료");

        // Create CounselingResult for the existing booking
        counselingResult = CounselingResult.createCounselingResult(
            student,
            instructor,
            booking,
            "Spring DI 개념에 대한 상담 결과입니다. 의존성 주입의 개념과 실제 활용 사례에 대해 논의했습니다.",
            LocalDateTime.of(2025, 3, 10, 11, 0) // After the consultation time
        );
        
        // Save using the repository instead of entity manager for better persistence
        counselingResult = counselingResultRepository.save(counselingResult);
        entityManager.flush();
        entityManager.clear(); // Clear persistence context to ensure data is actually persisted
    }

    @Test
    @DisplayName("Should verify initial dummy data exists")
    void verifyInitialSetup() {
        // Check if Backend class group exists
        ClassGroup backendClass = entityManager.find(ClassGroup.class, 1L);
        assertThat(backendClass).isNotNull();
        assertThat(backendClass.getName()).isEqualTo("Backend");

        // Check if student exists
        User student = entityManager.find(User.class, "uuid-be-001");
        assertThat(student).isNotNull();
        assertThat(student.getName()).isEqualTo("홍길동1");
        assertThat(student.getClassGroup()).isNotNull();
        assertThat(student.getClassGroup().getId()).isEqualTo(1L);

        // Check if instructor exists
        User instructor = entityManager.find(User.class, "uuid-be-ins");
        assertThat(instructor).isNotNull();
        assertThat(instructor.getName()).isEqualTo("이상우");
        assertThat(instructor.getRole()).isEqualTo("INSTRUCTOR");

        // Check if booking exists
        ConsultingBooking booking = entityManager.find(ConsultingBooking.class, 101L);
        assertThat(booking).isNotNull();
        assertThat(booking.getStatus()).isEqualTo("상담완료");
    }

    @Test
    @DisplayName("Should verify counseling result is saved correctly")
    void verifyCounselingResultSaved() {
        // Find the saved counseling result
        CounselingResult saved = entityManager.find(CounselingResult.class, counselingResult.getId());
        
        // Verify it exists and has correct data
        assertThat(saved).isNotNull();
        assertThat(saved.getStudent().getId()).isEqualTo("uuid-be-001");
        assertThat(saved.getInstructor().getId()).isEqualTo("uuid-be-ins");
        assertThat(saved.getBooking().getId()).isEqualTo(101L);
    }
} 