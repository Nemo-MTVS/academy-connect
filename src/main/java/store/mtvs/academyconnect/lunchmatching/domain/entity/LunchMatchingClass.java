package store.mtvs.academyconnect.lunchmatching.domain.entity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "lunch_matching_classes")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LunchMatchingClass {

    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "created_at")
    private String createdAt;

    @Column(name = "name", columnDefinition = "VARCHAR(255) NULL COMMENT 'BE, TA'")
    private String name;

    @OneToMany(mappedBy = "lunchMatchingClass", cascade = CascadeType.ALL)
    private List<LunchMatching> lunchMatchings = new ArrayList<>();

    @Builder
    public LunchMatchingClass(Long id, String createdAt, String name) {
        this.id = id;
        this.createdAt = createdAt;
        this.name = name;
    }
} 