package com.itninja.macropad.backend;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication
public class SseNotificationCoreApplication {
    public static void main(String[] args) {
        SpringApplication.run(SseNotificationCoreApplication.class, args);
        log.info("Swagger docs example URI: http://localhost:8080/sse/swagger-ui/index.html");
    }
}
