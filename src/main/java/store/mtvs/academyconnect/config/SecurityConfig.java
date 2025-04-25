package store.mtvs.academyconnect.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * 이 클래스는 Spring Security 설정을 위한 구성 클래스이며,
 * 현재는 테스트 목적을 위해 모든 요청에 대해 인증 없이 접근을 허용하고 있음.
 * CSRF 보안 기능도 비활성화되어 있고, 기본 HTTP Basic 인증이 설정만 되어 있음.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll()
                )
                .httpBasic(Customizer.withDefaults())
                .build();
    }
}


