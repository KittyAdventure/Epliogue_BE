package com.team1.epilogue;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = "jwt.secret=${jwt.secret}")
class EpilogueApplicationTests {

	@Test
	void contextLoads() {
	}

}
