package store.mtvs.academyconnect;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;
import store.mtvs.academyconnect.counselingresult.view.CreateCounselingView;

@SpringBootApplication
@EnableJpaAuditing
@EnableScheduling // 스케줄러 기능 활성화
public class AcademyConnectApplication {

    public static void main(String[] args) {
        SpringApplication.run(AcademyConnectApplication.class, args);
    }

    @Bean
    public CommandLineRunner runCounselingView(CreateCounselingView createCounselingView) {
        return args -> {
            System.out.println("\n=== Starting Counseling Result Management System ===\n");
            createCounselingView.showMenu();
        };
    }
}
