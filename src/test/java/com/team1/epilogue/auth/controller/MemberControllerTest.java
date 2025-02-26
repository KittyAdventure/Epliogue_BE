package com.team1.epilogue.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.team1.epilogue.auth.dto.RegisterRequest;
import com.team1.epilogue.auth.dto.MemberResponse;
import com.team1.epilogue.auth.security.CustomMemberDetails;
import com.team1.epilogue.auth.service.MemberService;
import com.team1.epilogue.auth.service.MemberWithdrawalService;
import com.team1.epilogue.auth.security.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

/**
 * [클래스 레벨]
 * 회원 등록과 회원 탈퇴 기능을 포함한 MemberController의 엔드포인트를 테스트하는 클래스
 * WebTestClient를 사용하여 API 요청/응답을 검증하며, MemberService, MemberWithdrawalService,
 * JwtTokenProvider를 모킹하여 컨트롤러 로직만 집중해서 확인
 */
@WebFluxTest(controllers = MemberController.class)
@DisplayName("MemberController 통합 테스트")
public class MemberControllerTest {

    /**
     * [필드 레벨]
     * 스프링 웹플럭스 환경에서 API 테스트를 수행하기 위한 클라이언트
     */
    @Autowired
    private WebTestClient webTestClient;

    /**
     * [필드 레벨]
     * JSON 직렬화/역징렬화를 담당
     */
    @Autowired
    private ObjectMapper objectMapper;

    /**
     * [필드 레벨]
     * 사용자 등록 기능을 담당하는 서비스의 모킹된 빈
     */
    @MockBean
    private MemberService memberService;

    /**
     * [필드 레벨]
     * 회원 탈퇴 기능을 담당하는 서비스의 모킹된 빈
     */
    @MockBean
    private MemberWithdrawalService memberWithdrawalService;

    /**
     * [필드 레벨]
     * JWT 토큰 검증 관련 로직을 담당하는 컴포넌트의 모킹된 빈
     */
    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    /**
     * [내부 클래스 레벨]
     * 테스트 환경에서 사용할 보안 설정을 구성
     */
    @TestConfiguration
    static class TestSecurityConfig {
        /**
         * [메서드 레벨]
         * CSRF를 비활성화하고 모든 요청을 허용하는 보안 설정을 구성하여 빈으로 등록
         *
         * @param http ServerHttpSecurity 객체
         * @return 구성된 SecurityWebFilterChain 빈
         */
        @Bean
        public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
            return http
                    .csrf(csrf -> csrf.disable())
                    .authorizeExchange(exchanges -> exchanges.anyExchange().permitAll())
                    .build();
        }
    }

    /**
     * [메서드 레벨]
     * 각 테스트 실행 전에 JwtTokenProvider의 validateToken 메서드를 모킹하여,
     * 어떤 토큰이 전달되더라도 true를 반환하도록 설정
     */
    @BeforeEach
    public void setUp() {
        when(jwtTokenProvider.validateToken(anyString())).thenReturn(true);
    }

    /**
     * [메서드 레벨]
     * /api/members/register 엔드포인트를 통해 회원 등록 기능을 테스트
     * 클라이언트가 전송한 JSON 형식의 RegisterRequest를 ObjectMapper로 변환 후 전송하고,
     * MemberService가 반환한 MemberResponse의 일부 필드(loginId, email)를 검증
     *
     * @throws Exception JSON 직렬화 중 발생할 수 있는 예외
     */
    @Test
    @DisplayName("정상 회원가입 요청 테스트")
    public void testRegisterMember() throws Exception {
        // 회원 등록 요청 데이터 생성
        RegisterRequest request = new RegisterRequest();
        request.setLoginId("testMember");
        request.setPassword("password123");
        request.setNickname("testNick");
        request.setName("Test Member");
        request.setBirthDate("1990-01-01");
        request.setEmail("test@example.com");
        request.setPhone("010-1111-2222");
        request.setProfileUrl("http://example.com/profile.jpg");

        // 예상 응답 객체 생성
        MemberResponse expectedResponse = MemberResponse.builder()
                .id("1")
                .loginId("testMember")
                .nickname("testNick")
                .name("Test Member")
                .birthDate("1990-01-01")
                .email("test@example.com")
                .phone("010-1111-2222")
                .profileUrl("http://example.com/profile.jpg")
                .build();

        // memberService.registerMember 호출 시 예상 응답 반환하도록 모킹
        when(memberService.registerMember(any(RegisterRequest.class))).thenReturn(expectedResponse);

        webTestClient.post()
                .uri("/api/members/register")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(objectMapper.writeValueAsString(request))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.loginId").isEqualTo("testMember")
                .jsonPath("$.email").isEqualTo("test@example.com");
    }

    /**
     * [메서드 레벨]
     *  /api/members 엔드포인트를 통해 정상적인 회원 탈퇴 기능을 테스트
     * SecurityMockServerConfigurers.mockUser를 사용해 인증된 사용자 정보를 SecurityContext에 주입하고,
     * 회원 탈퇴가 정상적으로 이루어지는지 검증
     */
    @Test
    @DisplayName("정상 회원 탈퇴 요청 테스트")
    public void testWithdrawMember() {
        Long memberId = 1L;
        // memberWithdrawalService.withdrawMember 호출 시 아무 작업도 수행하지 않도록 모킹
        doNothing().when(memberWithdrawalService).withdrawMember(memberId);

        // 테스트용 사용자 정보 생성
        CustomMemberDetails customUserDetails = new CustomMemberDetails(memberId, "testUser", "password", null);

        WebTestClient client = webTestClient.mutateWith(SecurityMockServerConfigurers.mockUser(customUserDetails));

        client.delete()
                .uri("/api/members")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.message").isEqualTo("유저의 계정이 정상적으로 삭제");
    }

    /**
     * [메서드 레벨]
     * 인증 정보 없이 /api/members 엔드포인트에 DELETE 요청을 보냈을 때,
     * 400 Bad Request 상태와 "인증되지 않은 사용자" 에러 메시지가 반환되는지 테스트
     */
    @Test
    @DisplayName("인증되지 않은 회원 탈퇴 요청 테스트")
    public void testWithdrawMemberUnauthorized() {
        webTestClient.delete()
                .uri("/api/members")
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.status").isEqualTo(400)
                .jsonPath("$.errorCode").isEqualTo("UNAUTHORIZED")
                .jsonPath("$.message").isEqualTo("인증되지 않은 사용자");
    }
}
