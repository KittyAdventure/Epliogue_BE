package com.team1.epilogue.auth.service;

import com.team1.epilogue.auth.dto.RegisterRequest;
import com.team1.epilogue.auth.dto.UserResponse;
import com.team1.epilogue.auth.exception.CustomException;
import com.team1.epilogue.auth.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class UserServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private RegisterRequest registerRequest;

    @BeforeEach
    public void setup() {
        registerRequest = new RegisterRequest();
        registerRequest.setUserId("serviceUser");
        registerRequest.setPassword("password123");
        registerRequest.setNickname("serviceNick");
        registerRequest.setName("Service User");
        registerRequest.setBirthdate("1990-01-01");
        registerRequest.setEmail("service@example.com");
        registerRequest.setPhone("010-1234-5678");
        registerRequest.setProfilePhoto("http://example.com/photo.jpg");
    }

    @Test
    public void testRegisterUserSuccess() {
        UserResponse response = userService.registerUser(registerRequest);
        assertNotNull(response);
        assertNotNull(response.getId());
        assertEquals("serviceUser", response.getUserId());

        // DB에 저장된 비밀번호는 암호화되어 있어야 함
        var savedUser = userRepository.findByUserId("serviceUser").orElse(null);
        assertNotNull(savedUser);
        assertTrue(passwordEncoder.matches("password123", savedUser.getPassword()));
    }

    @Test
    public void testDuplicateUserId() {
        // 최초 등록
        userService.registerUser(registerRequest);

        // 중복된 userId로 등록 시도
        RegisterRequest duplicate = new RegisterRequest();
        duplicate.setUserId("serviceUser");  // 중복
        duplicate.setPassword("password456");
        duplicate.setNickname("serviceNick2");
        duplicate.setName("Service User 2");
        duplicate.setBirthdate("1991-02-02");
        duplicate.setEmail("service2@example.com");
        duplicate.setPhone("010-2345-6789");
        duplicate.setProfilePhoto("http://example.com/photo2.jpg");

        CustomException ex = assertThrows(CustomException.class, () -> {
            userService.registerUser(duplicate);
        });
        assertEquals("이미 등록된 사용자 ID입니다.", ex.getMessage());
    }

    @Test
    public void testDuplicateEmail() {
        // 최초 등록
        userService.registerUser(registerRequest);

        // 중복된 email로 등록 시도
        RegisterRequest duplicate = new RegisterRequest();
        duplicate.setUserId("serviceUser2");
        duplicate.setPassword("password456");
        duplicate.setNickname("serviceNick2");
        duplicate.setName("Service User 2");
        duplicate.setBirthdate("1991-02-02");
        duplicate.setEmail("service@example.com");  // 중복된 email
        duplicate.setPhone("010-2345-6789");
        duplicate.setProfilePhoto("http://example.com/photo2.jpg");

        CustomException ex = assertThrows(CustomException.class, () -> {
            userService.registerUser(duplicate);
        });
        assertEquals("이미 등록된 이메일입니다.", ex.getMessage());
    }

    @Test
    public void testDuplicatePhone() {
        // 최초 등록
        userService.registerUser(registerRequest);

        // 중복된 핸드폰 번호로 등록 시도
        RegisterRequest duplicate = new RegisterRequest();
        duplicate.setUserId("serviceUser2");
        duplicate.setPassword("password456");
        duplicate.setNickname("serviceNick2");
        duplicate.setName("Service User 2");
        duplicate.setBirthdate("1991-02-02");
        duplicate.setEmail("service2@example.com");
        duplicate.setPhone("010-1234-5678");  // 중복된 핸드폰 번호
        duplicate.setProfilePhoto("http://example.com/photo2.jpg");

        CustomException ex = assertThrows(CustomException.class, () -> {
            userService.registerUser(duplicate);
        });
        assertEquals("이미 등록된 핸드폰 번호입니다.", ex.getMessage());
    }
}
