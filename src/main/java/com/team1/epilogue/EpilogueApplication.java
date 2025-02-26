package com.team1.epilogue;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
public class EpilogueApplication {

	public static void main(String[] args) {
		SpringApplication.run(EpilogueApplication.class, args);
	}

}
