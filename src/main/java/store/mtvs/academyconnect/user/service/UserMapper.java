package store.mtvs.academyconnect.user.service;

import org.springframework.stereotype.Service;
import store.mtvs.academyconnect.user.domain.entity.User;
import store.mtvs.academyconnect.user.dto.InstructorDTO;

@Service
public class UserMapper {

    public InstructorDTO toInstructorDTO(User user) {
        return new InstructorDTO(
                user.getId(),
                user.getName(),
                user.getClassGroup().getName(),
                user.getRole(),
                user.getProfile().getFilePath()
        );
    }
}
