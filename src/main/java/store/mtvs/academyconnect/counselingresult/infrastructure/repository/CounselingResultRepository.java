package store.mtvs.academyconnect.counselingresult.infrastructure.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import store.mtvs.academyconnect.counselingresult.domain.entity.CounselingResult;

import java.util.List;
import java.util.Optional;

public interface CounselingResultRepository extends JpaRepository<CounselingResult, Long> {

    @Query("""
        SELECT cr FROM CounselingResult cr
        JOIN FETCH cr.student
        JOIN FETCH cr.instructor
        LEFT JOIN FETCH cr.booking
        WHERE cr.id = :id AND cr.deletedAt IS NULL
    """)
    Optional<CounselingResult> findByIdWithUsers(@Param("id") Long id);

    @Query("""
        SELECT cr FROM CounselingResult cr
        JOIN FETCH cr.student
        JOIN FETCH cr.instructor
        LEFT JOIN FETCH cr.booking
        WHERE cr.student.id = :studentId AND cr.deletedAt IS NULL
        ORDER BY cr.counselAt DESC
    """)
    List<CounselingResult> findByStudentIdWithUsers(@Param("studentId") String studentId);

    @Query("""
        SELECT cr FROM CounselingResult cr
        JOIN FETCH cr.student
        JOIN FETCH cr.instructor
        LEFT JOIN FETCH cr.booking
        WHERE cr.instructor.id = :instructorId AND cr.deletedAt IS NULL
        ORDER BY cr.counselAt DESC
    """)
    List<CounselingResult> findByInstructorIdWithUsers(@Param("instructorId") String instructorId);

    @Query("""
        SELECT cr FROM CounselingResult cr
        JOIN FETCH cr.student
        JOIN FETCH cr.instructor
        WHERE cr.booking.id = :bookingId AND cr.deletedAt IS NULL
    """)
    Optional<CounselingResult> findByBookingIdWithUsers(@Param("bookingId") Long bookingId);
} 