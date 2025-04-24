package store.mtvs.academyconnect.user.domain.entity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import store.mtvs.academyconnect.classgroup.domain.entity.ClassGroup;
import store.mtvs.academyconnect.profile.domain.entity.Profile;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {

    @Id
    @Column(name = "id", columnDefinition = "VARCHAR(255) COMMENT 'UUID'")
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "class_group_id")
    private ClassGroup classGroup;

    @Column(name = "login_id", nullable = false, columnDefinition = "VARCHAR(255) COMMENT '암호화(복호화 가능)'")
    private String loginId;

    @Column(name = "passwd", nullable = false, columnDefinition = "VARCHAR(255) COMMENT '암호화'")
    private String password;

    @Column(name = "name", nullable = false, columnDefinition = "VARCHAR(255) COMMENT '암호화(복호화 가능)'")
    private String name;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updatedAt", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "deletedAt", nullable = false)
    private LocalDateTime deletedAt;

    @Column(name = "role", nullable = false)
    private String role;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Profile profile;

    @Builder
    public User(String id, ClassGroup classGroup, String loginId, String password, String name,
                LocalDateTime createdAt, LocalDateTime updatedAt, LocalDateTime deletedAt, String role) {
        this.id = id;
        this.classGroup = classGroup;
        this.loginId = loginId;
        this.password = password;
        this.name = name;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.deletedAt = deletedAt;
        this.role = role;
    }
} 