package store.mtvs.academyconnect.lunchmatching.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LunchMatchingPageController {

    /**
     * 점심 메이트 화면을 보여주는 메서드
     * URL: /lunchmatching
     * 파일 위치: templates/lunchmatching/lunchmatching.html
     */
    @GetMapping("/student/lunchmatching")
    public String lunchmatchingPage() {
        return "lunchmatching/lunchmatching";
        // templates/lunchmatching/lunchmatching.html을 찾아감
    }
}