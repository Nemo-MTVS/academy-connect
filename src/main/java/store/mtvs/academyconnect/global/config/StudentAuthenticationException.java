package store.mtvs.academyconnect.global.config;

import org.springframework.security.core.AuthenticationException;

public class StudentAuthenticationException extends AuthenticationException {
    public StudentAuthenticationException(String msg) {
        super(msg);
    }
    
    public StudentAuthenticationException(String msg, Throwable cause) {
        super(msg, cause);
    }
} 