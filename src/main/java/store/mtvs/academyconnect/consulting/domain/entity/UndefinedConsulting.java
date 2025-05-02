package store.mtvs.academyconnect.consulting.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "undefined_consultings")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UndefinedConsulting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "student_id", nullable = false, length = 255)
    private String studentId;

    @Column(name = "instructor_id", nullable = false, length = 255)
    private String instructorId;

    @Column(name = "request_at", nullable = false)
    private LocalDateTime requestAt;

    @Setter
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private RequestStatus status;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Lob
    @Column(name = "comment", columnDefinition = "TEXT")
    private String comment;

    @Builder
    public UndefinedConsulting(String studentId, String instructorId, LocalDateTime requestAt,
                               RequestStatus status, LocalDateTime updatedAt, String comment) {
        this.studentId = studentId;
        this.instructorId = instructorId;
        this.requestAt = requestAt;
        this.status = status;
        this.updatedAt = updatedAt;
        this.comment = comment;
    }

    public enum RequestStatus {
        WAITING, DONE
    }
}
