package store.mtvs.academyconnect.global.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import store.mtvs.academyconnect.user.domain.entity.User;
import store.mtvs.academyconnect.user.service.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomUserDetailService implements UserDetailsService {

    private final UserService userService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> userOptional = userService.getUserByLoginId(username);

        if (userOptional.isEmpty()) {
            throw new UsernameNotFoundException("아이디 또는 비밀번호가 일치하지 않습니다");
        }

        User user = userOptional.get();

        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_" + user.getRole()));

        return new CustomUserDetails(
                user.getLoginId(),
                user.getPassword(),
                authorities,
                user.getName(),
                user.getClassGroup().getName(),
                user.getRole(),
                user.getId()
        );
    }
}