package com.github.k.genki0913.verify;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing(auditorAwareRef = "auditorAware")
@SpringBootApplication
public class VerifyApplication {

    public static void main(String[] args) {
        SpringApplication.run(VerifyApplication.class, args);
    }

}
