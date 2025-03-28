package com.md.remo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootApplication
public class RemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(RemoApplication.class, args);
        log.info("Remo App started successfully");
	}

}
