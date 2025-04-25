package store.mtvs.academyconnect.consulting.domain.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import store.mtvs.academyconnect.consulting.domain.enums.UndefinedConsultingState;
import store.mtvs.academyconnect.user.domain.entity.User;

import java.time.LocalDateTime;

@Entity
@Table(name = "undefined_consultings")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class UndefinedConsulting {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private User student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "instructor_id", nullable = false)
    private User instructor;

    @Column(name = "request_at", nullable = false)
    private LocalDateTime requestAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private UndefinedConsultingState status;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Lob
    @Column(name = "comment")
    private String comment;

}
