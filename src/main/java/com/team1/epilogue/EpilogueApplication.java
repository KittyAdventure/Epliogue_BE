package com.team1.epilogue;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
@EnableMongoAuditing
public class EpilogueApplication {

	public static void main(String[] args) {
		SpringApplication.run(EpilogueApplication.class, args);
	}

}
