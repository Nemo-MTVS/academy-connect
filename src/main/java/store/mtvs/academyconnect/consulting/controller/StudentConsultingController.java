package store.mtvs.academyconnect.consulting.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/student")
public class StudentConsultingController {

    // 상담 예약 페이지
    @GetMapping("/consulting-booking")
    public String booking() {
        return "consulting/booking";
    }
    
    // 내 예약 보기 페이지
    @GetMapping("/consulting-my-bookings")
    public String myBookings() {
        return "consulting/my-bookings";
    }
}
