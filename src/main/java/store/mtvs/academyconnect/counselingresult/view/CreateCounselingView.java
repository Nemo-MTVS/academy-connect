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

    @Transactional
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
            
            // Get student UUID
            System.out.print("Enter student UUID (e.g., uuid-be-001): ");
            String studentUuid = scanner.nextLine();
            User student = userRepository.findById(studentUuid)
                .orElse(null);
            if (student == null) {
                System.out.println("❌ Student not found!");
                return;
            }
            System.out.println("✅ Found student: " + student.getName());

            // Get instructor UUID
            System.out.print("Enter instructor UUID (e.g., uuid-be-ins): ");
            String instructorUuid = scanner.nextLine();
            User instructor = userRepository.findById(instructorUuid)
                .orElse(null);
            if (instructor == null) {
                System.out.println("❌ Instructor not found!");
                return;
            }
            System.out.println("✅ Found instructor: " + instructor.getName());

            // Get booking ID
            System.out.print("Enter booking ID (e.g., 101) or press Enter to skip: ");
            String bookingInput = scanner.nextLine();
            ConsultingBooking booking = null;
            if (!bookingInput.trim().isEmpty()) {
                Long bookingId = Long.parseLong(bookingInput);
                booking = consultingBookingRepository.findById(bookingId)
                    .orElse(null);
                if (booking == null) {
                    System.out.println("❌ Booking not found!");
                    return;
                }
                System.out.println("✅ Found booking with ID: " + booking.getId());
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
                LocalDateTime.now()
            );

            System.out.println("Saving to database...");
            counselingResult = counselingResultRepository.save(counselingResult);
            System.out.println("Save completed.");
            
            System.out.println("✅ Created counseling result with ID: " + counselingResult.getId());

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
        scanner.nextLine(); // Consume newline

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
        System.out.println("✅ Counseling result deleted successfully!");
    }
}
