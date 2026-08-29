package com.github.k.genki0913.verify;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public final class VerifyApplication {

    private VerifyApplication() {
    }

    public static void main(String[] args) {
        SpringApplication.run(VerifyApplication.class, args);
    }

}
