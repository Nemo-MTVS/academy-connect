package store.mtvs.academyconnect.lunchmatching.domain.entity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import store.mtvs.academyconnect.user.domain.entity.User;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "lunch_matchings")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LunchMatching {

    @Id
    @Column(name = "id")
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lunch_matching_id")
    private LunchMatchingClass lunchMatchingClass;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Builder
    public LunchMatching(String id, LunchMatchingClass lunchMatchingClass, User user, LocalDateTime createdAt, LocalDateTime deletedAt) {
        this.id = id;
        this.lunchMatchingClass = lunchMatchingClass;
        this.user = user;
        this.createdAt = createdAt;
        this.deletedAt = deletedAt;
    }
} 