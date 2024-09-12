package com.ormi.mogakcote;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
public class MogakcoteApplication {

    public static void main(String[] args) {
        SpringApplication.run(MogakcoteApplication.class, args);
    }
}
