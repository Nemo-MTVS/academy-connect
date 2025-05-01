package store.mtvs.academyconnect.counselingresult.view;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import store.mtvs.academyconnect.consulting.domain.entity.ConsultingBooking;
import store.mtvs.academyconnect.consulting.infrastructure.repository.ConsultingBookingRepository;
import store.mtvs.academyconnect.counselingresult.domain.entity.CounselingResult;
import store.mtvs.academyconnect.counselingresult.infrastructure.repository.CounselingResultRepository;
import store.mtvs.academyconnect.user.domain.entity.User;
import store.mtvs.academyconnect.user.infrastructure.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Scanner;

@Component
@RequiredArgsConstructor
public class CreateCounselingView {

    private final CounselingResultRepository counselingResultRepository;
    private final UserRepository userRepository;
    private final ConsultingBookingRepository consultingBookingRepository;
    private final Scanner scanner = new Scanner(System.in);

    public void showMenu() {
        while (true) {
            System.out.println("\n=== Counseling Result Management ===");
            System.out.println("1. Create Counseling Result");
            System.out.println("2. View Single Counseling Result");
            System.out.println("3. View All Counseling Results");
            System.out.println("4. Update Counseling Result");
            System.out.println("5. Delete Counseling Result");
            System.out.println("6. Exit");
            System.out.print("\nEnter your choice: ");

            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    createCounselingResult();
                    break;
                case 2:
                    viewCounselingResult();
                    break;
                case 3:
                    viewAllCounselingResult();
                    break;
                case 4:
                    updateCounselingResult();
                    break;
                case 5:
                    deleteCounselingResult();
                    break;
                case 6:
                    System.out.println("Exiting...");
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    // 상담 결과 생성
    @Transactional
    protected void createCounselingResult() {
        try {
            System.out.println("\n=== Create Counseling Result ===");
            
            // Get Student name and handle multiple results
            System.out.print("Enter student name: ");
            String studentName = scanner.nextLine();
            List<User> matchingStudents = userRepository.findAllByName(studentName);
            
            if (matchingStudents.isEmpty()) {
                System.out.println("❌ No students found with that name!");
                return;
            }
            
            User student;
            if (matchingStudents.size() > 1) {
                System.out.println("\nMultiple students found with that name. Please select one:");
                for (int i = 0; i < matchingStudents.size(); i++) {
                    User s = matchingStudents.get(i);
                    System.out.printf("%d. %s (UUID: %s)%n", i + 1, s.getName(), s.getId());
                }
                
                System.out.print("\nEnter the number of the correct student: ");
                int selection = scanner.nextInt();
                scanner.nextLine(); // Consume newline
                
                if (selection < 1 || selection > matchingStudents.size()) {
                    System.out.println("❌ Invalid selection!");
                    return;
                }
                
                student = matchingStudents.get(selection - 1);
            } else {
                student = matchingStudents.get(0);
            }
            
            System.out.println("✅ Selected student: " + student.getName() + " (UUID: " + student.getId() + ")");

            // Get instructor name and handle multiple results
            System.out.print("\nEnter instructor name: ");
            String instructorName = scanner.nextLine();
            List<User> matchingInstructors = userRepository.findAllByName(instructorName);
            
            if (matchingInstructors.isEmpty()) {
                System.out.println("❌ No instructors found with that name!");
                return;
            }
            
            User instructor;
            if (matchingInstructors.size() > 1) {
                System.out.println("\nMultiple instructors found with that name. Please select one:");
                for (int i = 0; i < matchingInstructors.size(); i++) {
                    User ins = matchingInstructors.get(i);
                    System.out.printf("%d. %s (UUID: %s)%n", i + 1, ins.getName(), ins.getId());
                }
                
                System.out.print("\nEnter the number of the correct instructor: ");
                int selection = scanner.nextInt();
                scanner.nextLine(); // Consume newline
                
                if (selection < 1 || selection > matchingInstructors.size()) {
                    System.out.println("❌ Invalid selection!");
                    return;
                }
                
                instructor = matchingInstructors.get(selection - 1);
            } else {
                instructor = matchingInstructors.get(0);
            }
            
            System.out.println("✅ Selected instructor: " + instructor.getName() + " (UUID: " + instructor.getId() + ")");

            // Get booking ID
            System.out.print("Enter booking ID (e.g., 101) or press Enter to skip: ");
            ConsultingBooking booking = null;
            LocalDateTime counselAt = null;
            String bookingInput = scanner.nextLine();
            
            if (!bookingInput.trim().isEmpty()) {
                Long bookingId = Long.parseLong(bookingInput);
                booking = consultingBookingRepository.findById(bookingId)
                    .orElse(null);
                if (booking == null) {
                    System.out.println("❌ Booking not found!");
                    return;
                }
                counselAt = booking.getStartTime();
                System.out.println("✅ Found booking with ID: " + bookingId);
                System.out.println("✅ Using counseling time: " + counselAt);
            }

            // Get counseling result content
            System.out.println("Enter counseling result content (markdown):");
            String md = scanner.nextLine();

            System.out.println("Creating counseling result...");
            // Create and save counseling result
            CounselingResult counselingResult = CounselingResult.createCounselingResult(
                student,
                instructor,
                booking,
                md,
                counselAt
            );

            System.out.println("Saving to database...");
            counselingResult = counselingResultRepository.save(counselingResult);
            counselingResultRepository.flush(); // Force flush changes to DB
            System.out.println("Save completed.");
            
            System.out.println("✅ Created counseling result with ID: " + counselingResult.getId());
            if (counselAt != null) {
                System.out.println("✅ Saved with counseling time: " + counselingResult.getCounselAt());
            } else {
                System.out.println("ℹ️ No counseling time was set");
            }

        } catch (Exception e) {
            System.out.println("❌ Error creating counseling result");
            System.out.println("Error type: " + e.getClass().getName());
            System.out.println("Error message: " + e.getMessage());
            e.printStackTrace();
            throw e; // Re-throw to trigger transaction rollback
        }
    }
    // 상담 결과 상세 조회
    @Transactional(readOnly = true)
    protected void viewCounselingResult() {
        System.out.println("\n=== View Counseling Result ===");
        System.out.print("Enter counseling result ID: ");
        Long id = scanner.nextLong();
        scanner.nextLine(); // Consume newline

        CounselingResult result = counselingResultRepository.findByIdWithUsers(id)
            .orElse(null);
        if (result == null) {
            System.out.println("❌ Counseling result not found!");
            return;
        }

        System.out.println("\nCounseling Result Details:");
        System.out.println("ID: " + result.getId());
        System.out.println("Student: " + result.getStudent().getName());
        System.out.println("Instructor: " + result.getInstructor().getName());
        if (result.getBooking() != null) {
            System.out.println("Booking ID: " + result.getBooking().getId());
        }
        System.out.println("Content: " + result.getMd());
        System.out.println("Counsel At: " + result.getCounselAt());
        System.out.println("Created At: " + result.getCreatedAt());
        System.out.println("Updated At: " + result.getUpdatedAt());
        System.out.println("Deleted At: " + result.getDeletedAt());
    }

    // 상담 결과 전체 조회
    @Transactional(readOnly = true)
    protected void viewAllCounselingResult() {
        try {
            System.out.println("\n=== View All Counseling Results ===");
            System.out.println("Fetching results from database...");
            
            // Use findAll with eager loading of associations
            List<CounselingResult> results = counselingResultRepository.findAllWithUsers();
            
            if (results.isEmpty()) {
                System.out.println("No counseling results found.");
                System.out.println("\nPress Enter to return to menu...");
                scanner.nextLine();
                return;
            }
            
            System.out.println("\nTotal results: " + results.size());
            System.out.println("\n----------------------------------------");
            
            for (CounselingResult result : results) {
                try {
                    System.out.println("\nCounseling Result Details:");
                    System.out.println("ID: " + result.getId());
                    System.out.println("Student: " + result.getStudent().getName());
                    System.out.println("Instructor: " + result.getInstructor().getName());
                    if (result.getBooking() != null) {
                        System.out.println("Booking ID: " + result.getBooking().getId());
                    } else {
                        System.out.println("Booking ID: N/A (Impromptu session)");
                    }
                    System.out.println("Content: " + result.getMd());
                    System.out.println("Counsel At: " + result.getCounselAt());
                    System.out.println("----------------------------------------");
                } catch (Exception e) {
                    System.out.println("Error displaying result: " + e.getMessage());
                }
            }
            
            System.out.println("\nPress Enter to return to menu...");
            scanner.nextLine(); // Wait for user input before returning
            System.out.println("Returning to menu..."); // Debug log
            
        } catch (Exception e) {
            System.out.println("❌ Error fetching counseling results: " + e.getMessage());
            e.printStackTrace();
            System.out.println("\nPress Enter to return to menu...");
            scanner.nextLine();
        }
    }

    // 상담 결과 수정
    @Transactional
    protected void updateCounselingResult() {
        System.out.println("\n=== Update Counseling Result ===");
        System.out.print("Enter counseling result ID: ");
        Long id = scanner.nextLong();
        scanner.nextLine(); 

        CounselingResult result = counselingResultRepository.findByIdWithUsers(id)
            .orElse(null);
        if (result == null) {
            System.out.println("❌ Counseling result not found!");
            return;
        }

        System.out.println("Current content: " + result.getMd());
        System.out.println("Enter new content (or press Enter to keep current):");
        String newContent = scanner.nextLine();

        if (!newContent.trim().isEmpty()) {
            result.updateContent(newContent);
            counselingResultRepository.save(result);
            counselingResultRepository.flush(); // Force flush changes to DB
            System.out.println("✅ Counseling result updated successfully!");
        } else {
            System.out.println("No changes made.");
        }
    }

    // 상담 결과 삭제
    @Transactional
    protected void deleteCounselingResult() {
        System.out.println("\n=== Delete Counseling Result ===");
        System.out.print("Enter counseling result ID: ");
        Long id = scanner.nextLong();
        scanner.nextLine(); // Consume newline

        CounselingResult result = counselingResultRepository.findById(id)
            .orElse(null);
        if (result == null) {
            System.out.println("❌ Counseling result not found!");
            return;
        }

        result.delete();
        counselingResultRepository.save(result);
        counselingResultRepository.flush(); // Force flush changes to DB
        System.out.println("✅ Counseling result deleted successfully!");
    }
}