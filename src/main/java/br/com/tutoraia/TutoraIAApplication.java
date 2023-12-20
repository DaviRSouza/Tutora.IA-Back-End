package br.com.tutoraia;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TutoraIAApplication {

    public static void main(String[] args) {
        SpringApplication.run(TutoraIAApplication.class, args);
        System.out.println("API Running");
    }

}
