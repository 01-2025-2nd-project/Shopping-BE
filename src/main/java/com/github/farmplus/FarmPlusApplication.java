package com.github.farmplus;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class FarmPlusApplication {

	public static void main(String[] args) {
		SpringApplication.run(FarmPlusApplication.class, args);
	}

}
