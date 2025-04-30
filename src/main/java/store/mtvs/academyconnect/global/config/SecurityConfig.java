package store.mtvs.academyconnect.global.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import store.mtvs.academyconnect.global.filter.RequestDebugFilter;
import store.mtvs.academyconnect.user.domain.enums.UserRole;

import java.util.Collection;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomUserDetailService userDetailService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public RequestDebugFilter requestDebugFilter() {
        return new RequestDebugFilter();
    }

    // TEACHER용 로그인 성공 핸들러
    @Bean
    public AuthenticationSuccessHandler teacherAuthenticationSuccessHandler() {
        return (request, response, authentication) -> {
            response.sendRedirect("/teacher/scheduling");
        };
    }

    // STUDENT용 로그인 성공 핸들러
    @Bean
    public AuthenticationSuccessHandler studentAuthenticationSuccessHandler() {
        return (request, response, authentication) -> {
            response.sendRedirect("/");
        };
    }

    // TEACHER 권한 체크용 로그인 필터
    @Bean
    public AuthenticationFailureHandler teacherAuthenticationFailureHandler() {
        return (request, response, exception) -> {
            response.sendRedirect("/teacher/login?error=true");
        };
    }

    // STUDENT 권한 체크용 로그인 필터
    @Bean
    public AuthenticationFailureHandler studentAuthenticationFailureHandler() {
        return (request, response, exception) -> {
            response.sendRedirect("/login?error=true");
        };
    }
    
    // 로그아웃 성공 핸들러
    @Bean
    public CustomLogoutSuccessHandler customLogoutSuccessHandler() {
        return new CustomLogoutSuccessHandler();
    }

    // TEACHER 경로 전용 필터 체인 (우선순위 높음)
    @Bean
    @Order(1)
    public SecurityFilterChain teacherSecurityFilterChain(HttpSecurity http) throws Exception {
        http
            .securityMatcher("/teacher/**")
            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers("/teacher/login").permitAll()
                .anyRequest().hasRole(UserRole.TEACHER.name())
            )
            .formLogin(form -> form
                .loginPage("/teacher/login")
                .loginProcessingUrl("/teacher/login-process")
                .successHandler(teacherAuthenticationSuccessHandler())
                .failureHandler(teacherAuthenticationFailureHandler())
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/teacher/logout")
                .logoutSuccessHandler(customLogoutSuccessHandler())
                .invalidateHttpSession(true)
                .clearAuthentication(true)
                .deleteCookies("JSESSIONID")
            )
            .exceptionHandling(exception -> exception
                .accessDeniedPage("/haccess-denied")
            );

        // TEACHER 인증 처리를 위한 인증 관리자 설정
        AuthenticationManagerBuilder authBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
        authBuilder.userDetailsService(userDetailService).passwordEncoder(passwordEncoder());
        
        // TEACHER 로그인 시 TEACHER 권한만 허용하는 인증 관리자 구성
        http.authenticationManager(teacherAuthenticationManager(authBuilder.build()));

        return http.build();
    }

    // STUDENT 및 비로그인 사용자 경로 전용 필터 체인
    @Bean
    @Order(2)
    public SecurityFilterChain studentSecurityFilterChain(HttpSecurity http) throws Exception {
        http
            .securityMatcher("/**") // 나머지 모든 요청
            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers("/", "/login", "/signup", "/css/**", "/js/**", "/images/**").permitAll()
                .requestMatchers("/student/**").hasRole(UserRole.STUDENT.name())
                .anyRequest().hasRole(UserRole.STUDENT.name())
            )
            .formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/login-process")
                .successHandler(studentAuthenticationSuccessHandler())
                .failureHandler(studentAuthenticationFailureHandler())
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessHandler(customLogoutSuccessHandler())
                .invalidateHttpSession(true)
                .clearAuthentication(true)
                .deleteCookies("JSESSIONID")
            )
            .exceptionHandling(exception -> exception
                .accessDeniedPage("/access-denied")
            );

        // STUDENT 인증 처리를 위한 인증 관리자 설정
        AuthenticationManagerBuilder authBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
        authBuilder.userDetailsService(userDetailService).passwordEncoder(passwordEncoder());
        
        // STUDENT 로그인 시 STUDENT 권한만 허용하는 인증 관리자 구성
        http.authenticationManager(studentAuthenticationManager(authBuilder.build()));

        return http.build();
    }

    // TEACHER 권한만 인증할 수 있는 인증 관리자
    private AuthenticationManager teacherAuthenticationManager(AuthenticationManager authenticationManager) {
        return authentication -> {
            // 원래 인증 관리자로 인증 시도
            var auth = authenticationManager.authenticate(authentication);
            
            // 인증 성공했지만 TEACHER 권한이 아니면 인증 실패 처리
            Collection<? extends GrantedAuthority> authorities = auth.getAuthorities();
            if (!authorities.contains(new SimpleGrantedAuthority("ROLE_" + UserRole.TEACHER.name()))) {
                throw new TeacherAuthenticationException("TEACHER 권한이 없는 계정입니다.");
            }
            return auth;
        };
    }

    // STUDENT 권한만 인증할 수 있는 인증 관리자
    private AuthenticationManager studentAuthenticationManager(AuthenticationManager authenticationManager) {
        return authentication -> {
            // 원래 인증 관리자로 인증 시도
            var auth = authenticationManager.authenticate(authentication);
            
            // 인증 성공했지만 STUDENT 권한이 아니면 인증 실패 처리
            Collection<? extends GrantedAuthority> authorities = auth.getAuthorities();
            if (!authorities.contains(new SimpleGrantedAuthority("ROLE_" + UserRole.STUDENT.name()))) {
                throw new StudentAuthenticationException("STUDENT 권한이 없는 계정입니다.");
            }
            return auth;
        };
    }
}