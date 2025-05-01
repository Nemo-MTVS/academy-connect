package store.mtvs.academyconnect.user.domain.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import store.mtvs.academyconnect.classgroup.domain.entity.ClassGroup;
import store.mtvs.academyconnect.profile.domain.entity.Profile;

import jakarta.persistence.*;
import store.mtvs.academyconnect.user.domain.enums.UserRole;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
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
    @CreatedDate
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    @LastModifiedDate
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Column(name = "role", nullable = false)
    private String role = UserRole.STUDENT.name();

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Profile profile;

    public User(String id, ClassGroup classGroup, String loginId, String password, String name,
                 String role) {
        this.id = id;
        this.classGroup = classGroup;
        this.loginId = loginId;
        this.password = password;
        this.name = name;
        this.role = role;
    }
} 