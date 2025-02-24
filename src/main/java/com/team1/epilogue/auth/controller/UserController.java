package com.team1.epilogue.auth.controller;

import com.team1.epilogue.auth.dto.RegisterRequest;
import com.team1.epilogue.auth.dto.UserResponse;
import com.team1.epilogue.auth.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 클래스 레벨:
 * UserController는 사용자 관련 HTTP 요청을 처리하는 REST 컨트롤러
 * 이 클래스는 /api/users 경로로 들어오는 요청을 수신하고,
 * 사용자 등록과 관련된 비즈니스 로직을 UserService에 위임.
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    /**
     * UserService는 사용자 등록 및 기타 사용자 관련 기능을 담당하는 서비스 클래스.
     * @Autowired 어노테이션을 통해 스프링이 UserService 빈을 자동 주입.
     */
    @Autowired
    private UserService userService;

    /**
     * 사용자 등록 API 엔드포인트
     * 클라이언트가 JSON 형식의 사용자 등록 요청(RegisterRequest)을 전송하면,
     * 이를 검증(@Validated)하고, UserService를 호출하여 새로운 사용자를 등록한 후,
     * 결과로 UserResponse 객체 반환
     *
     * @param request 클라이언트가 전송한 사용자 등록 요청 데이터
     * @return 등록된 사용자의 정보를 담은 UserResponse 객체
     */
    @PostMapping(value = "/register", consumes = MediaType.APPLICATION_JSON_VALUE)
    public UserResponse registerUser(@Validated @RequestBody RegisterRequest request) {
        return userService.registerUser(request);
    }
}
