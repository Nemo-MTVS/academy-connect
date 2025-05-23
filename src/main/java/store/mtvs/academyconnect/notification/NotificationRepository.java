package store.mtvs.academyconnect.notification;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByTeacherIdOrderByCreatedAtDesc(String teacherId);
    List<Notification> findByTeacherIdAndIsReadFalse(String teacherId);
}
