package ca.jrvs.apps.trading;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "ca.jrvs.apps.trading")
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}