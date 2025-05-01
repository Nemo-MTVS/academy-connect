package store.mtvs.academyconnect.global.config;

import org.springframework.security.core.AuthenticationException;

public class TeacherAuthenticationException extends AuthenticationException {
    public TeacherAuthenticationException(String msg) {
        super(msg);
    }
    
    public TeacherAuthenticationException(String msg, Throwable cause) {
        super(msg, cause);
    }
} 