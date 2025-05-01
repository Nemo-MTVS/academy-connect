package store.mtvs.academyconnect.consulting.domain.entity;

import lombok.*;
import store.mtvs.academyconnect.user.domain.entity.User;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "consulting_slots")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ConsultingSlot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "instructor_id")
    private User instructor;

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime;

    @Column(name = "created_at", nullable = false, columnDefinition = "DATETIME NOT NULL COMMENT '등록시간 기준'")
    private LocalDateTime createdAt;

    @Setter
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Setter
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private SlotStatus status;

    @Builder
    public ConsultingSlot(Long id, User instructor, LocalDateTime startTime, LocalDateTime endTime,
                        LocalDateTime createdAt, LocalDateTime deletedAt, SlotStatus status) {
        this.id = id;
        this.instructor = instructor;
        this.startTime = startTime;
        this.endTime = endTime;
        this.createdAt = createdAt;
        this.deletedAt = deletedAt;
        this.status = status;
    }

    public enum SlotStatus {
        사용가능, 불가능
    }
} 