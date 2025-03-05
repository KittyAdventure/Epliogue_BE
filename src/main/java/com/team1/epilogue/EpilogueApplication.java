package com.team1.epilogue;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableScheduling
@SpringBootApplication
@EnableTransactionManagement
@EnableJpaAuditing
@EnableMongoAuditing
public class EpilogueApplication {
	public static void main(String[] args) {
		SpringApplication.run(EpilogueApplication.class, args);
	}
}
