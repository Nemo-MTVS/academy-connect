package store.mtvs.academyconnect.classgroup.domain.entity;

import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import store.mtvs.academyconnect.user.domain.entity.User;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "class_groups")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ClassGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, columnDefinition = "VARCHAR(255) COMMENT '백엔드'")
    private String name;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "expiredAt", nullable = false)
    private LocalDateTime expiredAt;

    @OneToMany(mappedBy = "classGroup", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<User> users = new ArrayList<>();

    public ClassGroup(String name, LocalDateTime createdAt, LocalDateTime expiredAt) {
        this.name = name;
        this.createdAt = createdAt;
        this.expiredAt = expiredAt;
    }
} 