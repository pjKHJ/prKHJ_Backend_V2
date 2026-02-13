package demago.khjv2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class KhJv2Application {

    public static void main(String[] args) {
        SpringApplication.run(KhJv2Application.class, args);
    }

}
