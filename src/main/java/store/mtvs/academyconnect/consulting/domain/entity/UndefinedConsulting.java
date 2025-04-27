package store.mtvs.academyconnect.consulting.domain.entity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import store.mtvs.academyconnect.user.domain.entity.User;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "undefined_consultings")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UndefinedConsulting {

    @Id
    @Column(name = "id")
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
    private RequestStatus status;
    
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    @Column(name = "comment")
    private String comment;

    @Builder
    public UndefinedConsulting(Long id, User student, User instructor, LocalDateTime requestAt, 
                              RequestStatus status, LocalDateTime updatedAt, String comment) {
        this.id = id;
        this.student = student;
        this.instructor = instructor;
        this.requestAt = requestAt;
        this.status = status;
        this.updatedAt = updatedAt;
        this.comment = comment;
    }

    public enum RequestStatus {
        WAITING, DONE
    }
}