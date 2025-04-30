package store.mtvs.academyconnect.consulting.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InstructorInfoForListDto {
    private String id;
    private String name;
    private String classGroupName;
    private String profileImageUrl;
}
