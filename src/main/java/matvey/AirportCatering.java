package matvey;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class AirportCatering {
    public static void main(String[] args) {
        SpringApplication.run(AirportCatering.class, args);
    }
}
