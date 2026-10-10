package ca.qc.cgodin.ggevenements;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class GgEvenementsApplication {

    public static void main(String[] args) {
        SpringApplication.run(GgEvenementsApplication.class, args);
    }
}
