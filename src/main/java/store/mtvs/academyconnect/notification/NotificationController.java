package store.mtvs.academyconnect.notification;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import store.mtvs.academyconnect.global.config.CustomUserDetails;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/teacher")
public class NotificationController {

    private final NotificationService notificationService;

    /**
     * [SSE 연결] 교사가 알림 수신을 위한 구독 요청
     */
    @GetMapping(value = "/notification/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @ResponseBody
    public SseEmitter subscribe(@RequestParam String studentId) {
        return notificationService.subscribe(studentId);
    }

    /**
     * [API] 알림 읽음 처리 (PATCH)
     */
    @PatchMapping("/notification/{id}/read")
    @ResponseBody
    public void markAsRead(@PathVariable Long id) {
        notificationService.markAsRead(id);
    }

    /**
     * [API] 전체 알림 목록 조회 (JSON) - 필요시
     */
    @GetMapping("/notification/list")
    @ResponseBody
    public List<Notification> getAll(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return notificationService.getAll(userDetails.getId());
    }

    /**
     * [API] 읽지 않은 알림 목록 조회 (JSON) - 필요시
     */
    @GetMapping("/notification/unread")
    @ResponseBody
    public List<Notification> getUnread(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return notificationService.getUnread(userDetails.getId());
    }

    /**
     * [VIEW] 강사 알림 페이지 렌더링 (/teacher/notifications)
     */
    @GetMapping("/notifications")
    public String viewTeacherNotifications(@AuthenticationPrincipal CustomUserDetails userDetails,
                                           Model model) {
        String teacherId = userDetails.getId();
        List<Notification> notifications = notificationService.getAll(teacherId);
        model.addAttribute("notifications", notifications);
        return "teacher/notifications";
    }
}
