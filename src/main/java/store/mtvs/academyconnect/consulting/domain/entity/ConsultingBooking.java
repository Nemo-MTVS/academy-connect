package store.mtvs.academyconnect.consulting.domain.entity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import store.mtvs.academyconnect.user.domain.entity.User;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "consulting_bookings")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ConsultingBooking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private User student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "instructor_id", nullable = false)
    private User instructor;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, columnDefinition = "ENUM('취소됨','상담완료','예약됨') NOT NULL DEFAULT '예약됨' COMMENT '예약 상태'")
    private BookingStatus status;

    @Column(name = "message", nullable = true, columnDefinition = "VARCHAR(255) NULL COMMENT '~~ 점이 궁금해요'") // 코멘트는 nullable
    private String message;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "update_at", nullable = false)
    private LocalDateTime updateAt;

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime;

    @Builder
    public ConsultingBooking(User student, User instructor, BookingStatus status, String message,
                             LocalDateTime createdAt, LocalDateTime updateAt, LocalDateTime startTime, LocalDateTime endTime) {
        this.student = student;
        this.instructor = instructor;
        this.status = status;
        this.message = message;
        this.createdAt = createdAt;
        this.updateAt = updateAt;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public enum BookingStatus {
        취소됨, 상담완료, 예약됨
    }
}