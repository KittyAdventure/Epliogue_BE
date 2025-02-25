package com.team1.epilogue.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.team1.epilogue.auth.dto.RegisterRequest;
import com.team1.epilogue.auth.dto.MemberResponse;
import com.team1.epilogue.auth.service.MemberService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * [클래스 레벨]
 * MemberControllerTest는 MemberController의 사용자 등록 API를 검증하는 테스트 클래스
 * Mockito를 사용하여 MemberService와 JwtTokenProvider의 동작을 모의(mock)하여 컨트롤러의 로직만 테스트
 */
@WebFluxTest(controllers = MemberController.class)
@Import(MemberControllerTest.TestSecurityConfig.class)
@DisplayName("MemberController 테스트")
public class MemberControllerTest {

    /**
     * [내부 클래스 - 테스트 전용 보안 설정]
     * TestSecurityConfig 클래스는 테스트 환경에서 보안 관련 설정을 커스터마이징하기 위한 설정 클래스로,
     * 모든 HTTP 요청에 대해 인증 없이 접근할 수 있도록 CSRF 보호를 비활성화하고, 모든 요청을 허용
     */
    @TestConfiguration
    static class TestSecurityConfig {
        @Bean
        public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
            return http
                    .csrf(csrf -> csrf.disable()) // CSRF 보호 비활성화
                    .authorizeExchange(exchanges -> exchanges.anyExchange().permitAll()) // 모든 요청 허용
                    .build();
        }
    }

    /**
     * [필드 레벨]
     * WebFlux 기반의 비동기 웹 애플리케이션 테스트를 위한 클라이언트
     * 컨트롤러에 대한 HTTP 요청을 시뮬레이션하는 데 사용
     */
    @org.springframework.beans.factory.annotation.Autowired
    private WebTestClient webTestClient;

    /**
     * [필드 레벨]
     * Java 객체와 JSON 간의 직렬화/역직렬화를 담당하는 컴포넌트
     */
    @org.springframework.beans.factory.annotation.Autowired
    private ObjectMapper objectMapper;

    /**
     * [필드 레벨]
     * MemberController의 의존성으로, 실제 빈 대신 Mockito가 제공하는 Mock Bean으로 주입
     */
    @MockBean
    private MemberService memberService;

    /**
     * [필드 레벨]
     * JWT 토큰과 관련된 보안 컴포넌트로, Mock Bean으로 등록
     * (MemberController에서는 직접 사용하지 않더라도, Security 설정에 의해 주입되어야 할 수 있음)
     */
    @MockBean
    private com.team1.epilogue.auth.security.JwtTokenProvider jwtTokenProvider;

    /**
     * [필드 레벨]
     * 각 테스트 케이스에서 사용할 사용자 등록 데이터를 담은 DTO 객체
     */
    private RegisterRequest registerRequest;

    /**
     * [메서드 레벨]
     * setup 메서드는 각 테스트 실행 전에 registerRequest 객체를 초기화
     * 테스트에 사용할 기본 사용자 등록 정보를 설정
     */
    @BeforeEach
    public void setup() {
        registerRequest = new RegisterRequest();
        registerRequest.setLoginId("controllerMember");    // 사용자 로그인 ID 설정
        registerRequest.setPassword("password123");          // 비밀번호 설정
        registerRequest.setNickname("controllerNick");       // 사용자 닉네임 설정
        registerRequest.setName("Controller Member");        // 사용자 이름 설정
        registerRequest.setBirthDate("1990-01-01");            // 생년월일 설정 (문자열 형태)
        registerRequest.setEmail("controller@example.com");    // 이메일 설정
        registerRequest.setPhone("010-1234-5678");             // 전화번호 설정
        registerRequest.setProfileUrl("http://example.com/photo.jpg"); // 프로필 사진 URL 설정
    }

    /**
     * [메서드 레벨]
     * testRegisterMember 메서드는 /api/members/register 엔드포인트를 통해 사용자 등록 기능을 테스트
     * Mockito를 사용하여 memberService.registerMember() 메서드의 호출 결과를 미리 설정한 후,
     * HTTP POST 요청을 통해 실제 응답이 예상한 값과 일치하는지 검증
     *
     * @throws Exception JSON 변환 과정에서 발생할 수 있는 예외 던짐
     */
    @Test
    @DisplayName("정상 회원가입 요청 테스트")
    public void testRegisterMember() throws Exception {
        // given: 예상 응답 객체 생성
        MemberResponse expectedResponse = MemberResponse.builder()
                .id("1")
                .loginId("controllerMember")
                .nickname("controllerNick")
                .name("Controller Member")
                .birthDate("1990-01-01")
                .email("controller@example.com")
                .phone("010-1234-5678")
                .profileUrl("http://example.com/photo.jpg")
                .build();

        // memberService.registerMember 호출 시 예상 응답을 반환하도록 모의 설정
        when(memberService.registerMember(any(RegisterRequest.class))).thenReturn(expectedResponse);

        // when & then: POST 요청을 보내고, 응답의 loginId 및 email 값이 예상과 일치하는지 검증
        webTestClient.post()
                .uri("/api/members/register")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(objectMapper.writeValueAsString(registerRequest))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.loginId").isEqualTo("controllerMember")
                .jsonPath("$.email").isEqualTo("controller@example.com");
    }
}
