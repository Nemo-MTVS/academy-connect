package store.mtvs.academyconnect;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TeacherTempController {

    @GetMapping("/teacherss")
    public String test() {
        return "instructor/schedule";
    }

    @GetMapping("/teacherss/reservation")
    public String test2() {
        return "instructor/reservation";
    }

    @GetMapping("/teacherss/schedule")
    public String test3() {
        return "teacher";
    }

}

