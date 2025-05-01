package store.mtvs.academyconnect.profile.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class ProfileDTO {
    private String email;
    private String github;
    private String blog;
    private String bio;
    private MultipartFile profileImage;

    public ProfileDTO(String email, String github, String blog, String bio, MultipartFile profileImage) {
        this.email = email;
        this.github = github;
        this.blog = blog;
        this.bio = bio;
        this.profileImage = profileImage;
    }

}
