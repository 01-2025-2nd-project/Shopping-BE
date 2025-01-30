package com.github.farmplus;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
@EnableJpaAuditing
public class FarmPlusApplication {

	public static void main(String[] args) {
		SpringApplication.run(FarmPlusApplication.class, args);
	}

	// CommandLineRunner를 사용하여 비밀번호 암호화 실행
//	@Bean
//	public CommandLineRunner run() {
//		return args -> {
//			PasswordEncoder encoder = new BCryptPasswordEncoder();
//			String rawPassword = "test1234"; // 원본 비밀번호
//			String encodedPassword = encoder.encode(rawPassword); // 암호화된 비밀번호
//			System.out.println("Encoded password: " + encodedPassword); // 출력
//		};
//	}

}