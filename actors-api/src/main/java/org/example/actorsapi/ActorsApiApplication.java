package org.example.actorsapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.reactive.config.EnableWebFlux;

@SpringBootApplication
@EnableWebFlux
public class ActorsApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(ActorsApiApplication.class, args);
    }

}
