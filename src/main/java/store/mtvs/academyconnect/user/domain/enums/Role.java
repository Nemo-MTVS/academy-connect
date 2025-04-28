package store.mtvs.academyconnect.user.domain.enums;

/**
 * 사용자 권한을 정의하는 enum 클래스
 */
public enum Role {
    TEACHER("TEACHER"), STUDENT("STUDENT");
    
    private final String value;
    
    Role(String value) {
        this.value = value;
    }
    
    public String getValue() {
        return value;
    }
} 