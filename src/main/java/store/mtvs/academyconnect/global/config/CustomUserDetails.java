package store.mtvs.academyconnect.global.config;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

@Getter
public class CustomUserDetails implements UserDetails {
    private final String id;
    private final String username;
    private final String password;
    private final List<GrantedAuthority> authorities;
    private final String name;
    private final String classGroupName;
    private final String role;

    public CustomUserDetails(String username, String password, List<GrantedAuthority> authorities,
                             String name, String classGroupName, String role, String id) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.authorities = authorities;
        this.name = name;
        this.classGroupName = classGroupName;
        this.role = role;
    }

    @Override
    public List<GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }
}
