package com.team1.epilogue.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.team1.epilogue.auth.dto.RegisterRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(properties = {"jwt.secret=defaultSecretKey"})
@AutoConfigureWebTestClient
public class UserControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    private RegisterRequest registerRequest;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setup() {
        registerRequest = new RegisterRequest();
        registerRequest.setUserId("controllerUser");
        registerRequest.setPassword("password123");
        registerRequest.setNickname("controllerNick");
        registerRequest.setName("Controller User");
        registerRequest.setBirthdate("1990-01-01");
        registerRequest.setEmail("controller@example.com");
        registerRequest.setPhone("010-1234-5678");
        registerRequest.setProfilePhoto("http://example.com/photo.jpg");
    }

    @Test
    public void testRegisterUser() throws Exception {
        webTestClient.post()
                .uri("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(objectMapper.writeValueAsString(registerRequest))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.userId").isEqualTo("controllerUser")
                .jsonPath("$.email").isEqualTo("controller@example.com");
    }
}
