package store.mtvs.academyconnect.classgroup.domain.entity;

import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
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
    @Column(name = "id")
    private Long id;

    @Column(name = "name", nullable = false, columnDefinition = "VARCHAR(255) COMMENT '백엔드'")
    private String name;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "expiredAt", nullable = false)
    private LocalDateTime expiredAt;

    @OneToMany(mappedBy = "classGroup", cascade = CascadeType.ALL)
    private List<User> users = new ArrayList<>();

    @Builder
    public ClassGroup(Long id, String name, LocalDateTime createdAt, LocalDateTime expiredAt) {
        this.id = id;
        this.name = name;
        this.createdAt = createdAt;
        this.expiredAt = expiredAt;
    }
} 