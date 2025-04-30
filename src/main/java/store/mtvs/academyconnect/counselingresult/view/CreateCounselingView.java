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
            System.out.println("2. View Counseling Result");
            System.out.println("3. Update Counseling Result");
            System.out.println("4. Delete Counseling Result");
            System.out.println("5. Exit");
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
                    updateCounselingResult();
                    break;
                case 4:
                    deleteCounselingResult();
                    break;
                case 5:
                    System.out.println("Exiting...");
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    @Transactional
    protected void createCounselingResult() {
        try {
            System.out.println("\n=== Create Counseling Result ===");

            // Get Student name
            System.out.print("Enter student name: ");
            String studentName = scanner.nextLine();
            User student = userRepository.findByName(studentName)
                .orElse(null);
            if (student == null) {
                System.out.println("❌ Student not found!");
                return;
            }
            System.out.println("✅ Found student: " + studentName);

            // Get Student UUID from student name
            String studentUuid = student.getId();
            System.out.println("✅ Found student UUID: " + studentUuid);

            // Get instructor name
            System.out.print("Enter instructor name: ");
            String instructorName = scanner.nextLine();
            User instructor = userRepository.findByName(instructorName)
                .orElse(null);
            if (instructor == null) {
                System.out.println("❌ Instructor not found!");
                return;
            }
            System.out.println("✅ Found instructor: " + instructorName);

            // Get instructor UUID from instructor name
            String instructorUuid = instructor.getId();
            System.out.println("✅ Found instructor UUID: " + instructorUuid);

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