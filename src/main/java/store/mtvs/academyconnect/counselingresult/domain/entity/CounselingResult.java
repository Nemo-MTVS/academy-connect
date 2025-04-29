package store.mtvs.academyconnect.counselingresult.domain.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import store.mtvs.academyconnect.consulting.domain.entity.ConsultingBooking;
import store.mtvs.academyconnect.user.domain.entity.User;

import java.time.LocalDateTime;

@Entity
@Table(name = "counseling_results")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CounselingResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private User student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "instructor_id", nullable = false)
    private User instructor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id")
    private ConsultingBooking booking;

    @Column(columnDefinition = "LONGTEXT")
    private String md;

    @Column(name = "counsel_at", nullable = false)
    private LocalDateTime counselAt;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    public static CounselingResult createCounselingResult(
            User student,
            User instructor,
            ConsultingBooking booking,
            String md,
            LocalDateTime counselAt
    ) {
        CounselingResult result = new CounselingResult();
        result.student = student;
        result.instructor = instructor;
        result.booking = booking;
        result.md = md;
        result.counselAt = counselAt;
        result.createdAt = LocalDateTime.now();
        result.updatedAt = LocalDateTime.now();
        return result;
    }

    public void updateContent(String md) {
        this.md = md;
        this.updatedAt = LocalDateTime.now();
    }

    public void delete() {
        this.deletedAt = LocalDateTime.now();
    }
} 