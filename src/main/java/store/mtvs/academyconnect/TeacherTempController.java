package store.mtvs.academyconnect;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TeacherTempController {

    @GetMapping("/teacher")
    public String test() {
        return "teacher";
    }

    @GetMapping("/teacher/reservation")
    public String test2() {
        return "reservation";
    }

    @GetMapping("/teacher/schedule")
    public String test3() {
        return "teacher";
    }

}

