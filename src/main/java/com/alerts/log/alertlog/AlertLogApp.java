package com.alerts.log.alertlog;

import com.alerts.log.alertlog.service.LogAppService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.time.Duration;
import java.time.Instant;

@SpringBootApplication
public class AlertLogApp implements CommandLineRunner {
    private static final Logger LOGGER = LoggerFactory.getLogger(AlertLogApp.class);

    @Autowired
    private LogAppService service;

    public static void main(String... args) {
        SpringApplication app = new SpringApplication(AlertLogApp.class);
        app.run(args);
       // LOGGER.info("User Dir : "+System.getProperty("user.dir")); -- project running path
    }

    @Override
    public void run(String... args) {
        Instant start = Instant.now();
        service.execute(args);
        Instant end = Instant.now();
        LOGGER.info("Total time: {}ms", Duration.between(start, end).toMillis());

    }
}
