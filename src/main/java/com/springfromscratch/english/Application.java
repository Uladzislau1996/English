package com.springfromscratch.english;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = {"telegram", "ai", "premium", "payment", "user"})
@EnableJpaRepositories(basePackages = "user")
@EntityScan(basePackages = "user")
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
