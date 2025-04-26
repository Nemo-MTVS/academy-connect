package store.mtvs.academyconnect.profile.domain.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import store.mtvs.academyconnect.user.domain.entity.User;

import jakarta.persistence.*;

@Entity
@Table(name = "profiles")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Profile {

    @Id
    @Column(name = "id", columnDefinition = "VARCHAR(255) COMMENT 'UUID'")
    private String id;

    @Lob
    @Column(name = "md")
    private String md;

    @Column(name = "github")
    private String github;

    @Column(name = "blog")
    private String blog;

    @Column(name = "email")
    private String email;

    @Column(name = "file_path")
    private String filePath;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id")
    @MapsId
    private User user;

    public Profile(String md, String github, String blog, String email, String filePath, User user) {
        this.id = user.getId();
        this.md = md;
        this.github = github;
        this.blog = blog;
        this.email = email;
        this.filePath = filePath;
        this.user = user;
    }
} 