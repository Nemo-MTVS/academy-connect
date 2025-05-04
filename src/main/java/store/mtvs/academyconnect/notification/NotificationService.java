package store.mtvs.academyconnect.notification;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {
    private final NotificationRepository notificationRepository;

    // 사용자별 emitter 저장소
    private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();

    /**
     * 클라이언트에서 SSE 연결 요청 시 emitter 생성
     */
    public SseEmitter subscribe(String userId) {
        // SseEmitter emitter = new SseEmitter(60 * 1000L); // 60초 타임아웃
        SseEmitter emitter = new SseEmitter(0L); // 60초 타임아웃
        log.info("Subscribing to user {}", userId);
        emitters.put(userId, emitter);
        log.info("SseEmitter created for userId: {}", userId);

        emitter.onCompletion(() -> {
            log.info("SseEmitter completed for userId: {}", userId);
            emitters.remove(userId);
        });
        emitter.onTimeout(() -> {
            log.info("SseEmitter timed out for userId: {}", userId);
            emitters.remove(userId);
        });
        emitter.onError((e) -> {
            log.error("SseEmitter error for userId: {} - {}", userId, e.getMessage());
            emitters.remove(userId);
        });

        return emitter;
    }

    /**
     * 알림 저장 및 실시간 전송
     */
    public void send(String src, String des, String message) {
        Notification notification = new Notification(src, des, message);
        notificationRepository.save(notification);

        SseEmitter emitter = emitters.get(des);
        log.info("Sending notification to teacherId: {}", des);
        log.info("emitter: {}", emitter);
        if (emitter != null) {
            try {
                log.info("사용자에게 알림 발송: {}", des);
                emitter.send(SseEmitter.event()
                        .name("notification")
                        .data(message));
            } catch (IOException e) {
                emitter.completeWithError(e);
                emitters.remove(des);
            }
        }
    }

    /**
     * 알림 읽음 처리
     */
    public void read(String userId, Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new IllegalArgumentException("Notification not found"));
        notification.markAsRead();
        notificationRepository.save(notification);
    }

    public void markAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new IllegalArgumentException("알림을 찾을 수 없습니다"));
        notification.markAsRead();
        notificationRepository.save(notification);
    }

    public List<Notification> getAll(String teacherId) {
        return notificationRepository.findByTeacherIdOrderByCreatedAtDesc(teacherId);
    }

    public List<Notification> getUnread(String teacherId) {
        return notificationRepository.findByTeacherIdAndIsReadFalse(teacherId);
    }
}
