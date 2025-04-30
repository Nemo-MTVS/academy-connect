package store.mtvs.academyconnect.lunchmatching.dto;

/**
 * 점심 매칭 학생 1명의 정보 DTO
 * - 이름
 * - 전공
 * - 사용자 ID
 */
public class StudentInfo {

    private String name;    // 이름
    private String major;   // 전공
    private String userId;  // 유저 ID

    public StudentInfo(String name, String major, String userId) {
        this.name = name;
        this.major = major;
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public String getMajor() {
        return major;
    }

    public String getUserId() {
        return userId;
    }
}
