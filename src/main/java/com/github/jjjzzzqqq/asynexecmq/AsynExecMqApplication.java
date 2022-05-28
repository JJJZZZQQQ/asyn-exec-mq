package com.github.jjjzzzqqq.asynexecmq;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class AsynExecMqApplication {

    public static void main(String[] args) {
        SpringApplication.run(AsynExecMqApplication.class, args);
    }

}
