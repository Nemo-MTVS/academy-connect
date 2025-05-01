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
import org.springframework.web.filter.HiddenHttpMethodFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomUserDetailService userDetailService;

    /* ---------- 공통 Bean ---------- */

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public HiddenHttpMethodFilter hiddenHttpMethodFilter() {
        return new HiddenHttpMethodFilter();
    }

    @Bean
    public RequestDebugFilter requestDebugFilter() {
        return new RequestDebugFilter();   // 아래 2-항 참고
    }

    @Bean
    public CustomLogoutSuccessHandler customLogoutSuccessHandler() {
        return new CustomLogoutSuccessHandler();
    }

    /* ---------- TEACHER ---------- */

    @Bean
    public AuthenticationSuccessHandler teacherAuthSuccessHandler() {
        return (req, res, auth) -> res.sendRedirect("/teacher/scheduling");
    }

    @Bean
    public AuthenticationFailureHandler teacherAuthFailureHandler() {
        return (req, res, ex) -> res.sendRedirect("/teacher/login?error=true");
    }

    @Bean
    @Order(1)
    public SecurityFilterChain teacherSecurityFilterChain(HttpSecurity http) throws Exception {
        http.securityMatcher("/teacher/**")
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/teacher/login",
                                "/haccess-denied"         // ★ access-denied 페이지는 무조건 permitAll
                        ).permitAll()
                        .anyRequest().hasRole(UserRole.TEACHER.name())
                )
                .formLogin(form -> form
                        .loginPage("/teacher/login")
                        .loginProcessingUrl("/teacher/login-process")
                        .successHandler(teacherAuthSuccessHandler())
                        .failureHandler(teacherAuthFailureHandler())
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/teacher/logout")
                        .logoutSuccessHandler(customLogoutSuccessHandler())
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                        .deleteCookies("JSESSIONID")
                )
                .exceptionHandling(ex -> ex
                        .accessDeniedPage("/haccess-denied")
                )
            .csrf(csrf -> csrf
                .ignoringRequestMatchers("/teacher/counselingresult/**"));
                // .csrf(AbstractHttpConfigurer::disable);

        /* 인증 관리자 구성 */
        AuthenticationManagerBuilder builder = http.getSharedObject(AuthenticationManagerBuilder.class);
        builder.userDetailsService(userDetailService).passwordEncoder(passwordEncoder());

        http.authenticationManager(teacherOnlyManager(builder.build()));
        return http.build();
    }

    /* ---------- STUDENT + 비로그인 ---------- */

    @Bean
    public AuthenticationSuccessHandler studentAuthSuccessHandler() {
        return (req, res, auth) -> res.sendRedirect("/");
    }

    @Bean
    public AuthenticationFailureHandler studentAuthFailureHandler() {
        return (req, res, ex) -> res.sendRedirect("/login?error=true");
    }

    @Bean
    @Order(2)
    public SecurityFilterChain studentSecurityFilterChain(HttpSecurity http) throws Exception {

        http.securityMatcher("/**")
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/", "/login", "/signup",
                                "/css/**", "/js/**", "/images/**",
                                "/access-denied"           // ★ permitAll
                        ).permitAll()
                        .requestMatchers("/student/**").hasRole(UserRole.STUDENT.name())
                        .anyRequest().hasRole(UserRole.STUDENT.name())
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/login-process")
                        .successHandler(studentAuthSuccessHandler())
                        .failureHandler(studentAuthFailureHandler())
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessHandler(customLogoutSuccessHandler())
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                        .deleteCookies("JSESSIONID")
                )
                .exceptionHandling(ex -> ex
                        .accessDeniedPage("/access-denied")
                );
                // .csrf(AbstractHttpConfigurer::disable);

        AuthenticationManagerBuilder builder = http.getSharedObject(AuthenticationManagerBuilder.class);
        builder.userDetailsService(userDetailService).passwordEncoder(passwordEncoder());

        http.authenticationManager(studentOnlyManager(builder.build()));
        return http.build();
    }

    /* ---------- 전용 AuthenticationManager ---------- */

    private static final GrantedAuthority ROLE_TEACHER =
            new SimpleGrantedAuthority("ROLE_" + UserRole.TEACHER.name());
    private static final GrantedAuthority ROLE_STUDENT =
            new SimpleGrantedAuthority("ROLE_" + UserRole.STUDENT.name());

    private AuthenticationManager teacherOnlyManager(AuthenticationManager base) {
        return auth -> {
            var result = base.authenticate(auth);
            if (!result.getAuthorities().contains(ROLE_TEACHER)) {
                throw new TeacherAuthenticationException("TEACHER 권한이 없습니다.");
            }
            return result;
        };
    }

    private AuthenticationManager studentOnlyManager(AuthenticationManager base) {
        return auth -> {
            var result = base.authenticate(auth);
            if (!result.getAuthorities().contains(ROLE_STUDENT)) {
                throw new StudentAuthenticationException("STUDENT 권한이 없습니다.");
            }
            return result;
        };
    }
}
