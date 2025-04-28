package store.mtvs.academyconnect.profile.domain.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import store.mtvs.academyconnect.classgroup.domain.entity.ClassGroup;
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "class_group_id")
    private ClassGroup classGroup;

    public Profile(String md, String github, String blog, String email, String filePath, User user, ClassGroup classGroup) {
        this.id = user.getId();
        this.md = md;
        this.github = github;
        this.blog = blog;
        this.email = email;
        this.filePath = filePath;
        this.user = user;
        this.classGroup = classGroup;
    }


    public void setId(String id) {
        this.id = id;
    }


    public void setMd(String md) {
        this.md = md;
    }
    public void setGithub(String github) {
        this.github = github;
    }


    public void setBlog(String blog) {
        this.blog = blog;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }


    public void setUser(User user) {
        this.user = user;
    }


    public void setClassGroup(ClassGroup classGroup) {
        this.classGroup = classGroup;
    }
}