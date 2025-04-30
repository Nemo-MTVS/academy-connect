package store.mtvs.academyconnect.lunchmatching.dto;

import java.util.List;

/**
 * 점심 매칭 현황 조회 결과를 담는 DTO 클래스
 *
 * 매칭 클래스별:
 * - 클래스 ID
 * - 클래스 이름
 * - 현재 신청 인원 수
 * - 신청자 리스트 (이름 + 전공 + userId)
 */
public class LunchMatchingStatusResponse {

    private Long classId;
    private String className;
    private int currentCount;
    private List<StudentInfo> students;

    public LunchMatchingStatusResponse(Long classId, String className, int currentCount, List<StudentInfo> students) {
        this.classId = classId;
        this.className = className;
        this.currentCount = currentCount;
        this.students = students;
    }

    public Long getClassId() {
        return classId;
    }

    public String getClassName() {
        return className;
    }

    public int getCurrentCount() {
        return currentCount;
    }

    public List<StudentInfo> getStudents() {
        return students;
    }
}
