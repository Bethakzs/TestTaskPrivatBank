package org.example.testtaskprivatbank;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableScheduling;


@SpringBootApplication
@EnableScheduling
public class TestTaskPrivatBankApplication {

    public static void main(String[] args) {
        SpringApplication.run(TestTaskPrivatBankApplication.class, args);
    }

}
