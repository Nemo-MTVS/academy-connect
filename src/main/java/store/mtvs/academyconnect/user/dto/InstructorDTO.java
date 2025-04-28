package store.mtvs.academyconnect.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InstructorDTO {
    private String instructorId;
    private String name;
    private String classGroupName;
    private String role;
    private String profileImageUrl;
}
