package store.mtvs.academyconnect;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class AcademyConnectApplication {

    public static void main(String[] args) {
        SpringApplication.run(AcademyConnectApplication.class, args);
    }

}
