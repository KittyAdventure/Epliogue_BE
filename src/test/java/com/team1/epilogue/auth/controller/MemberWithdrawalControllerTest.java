package com.team1.epilogue.auth.controller;

import com.team1.epilogue.auth.security.CustomMemberDetails;
import com.team1.epilogue.auth.security.JwtTokenProvider;
import com.team1.epilogue.auth.service.MemberWithdrawalService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

/**
 * [클래스 레벨]
 * MemberWithdrawalController의 엔드포인트를 테스트하는 클래스
 * 이 클래스에서는 WebTestClient를 사용해서 API 요청과 응답을 검증
 * 또한, MemberWithdrawalService와 JwtTokenProvider는 실제 구현 대신 모킹(mocking)해서 테스트 대상인 컨트롤러 로직만 집중해서 확인
 */
@WebFluxTest(controllers = MemberWithdrawalController.class)
@DisplayName("MemberWithdrawalController 테스트")
public class MemberWithdrawalControllerTest {
//
//    /**
//     * [필드 레벨]
//     * 스프링 웹플럭스 환경에서 API 테스트를 수행하기 위한 클라이언트
//     */
//    @Autowired
//    private WebTestClient webTestClient;
//
//    /**
//     * [필드 레벨]
//     * 회원 탈퇴 비즈니스 로직을 담당하는 서비스의 모킹된 빈
//     */
//    @MockitoBean
//    private MemberWithdrawalService memberWithdrawalService;
//
//    /**
//     * [필드 레벨]
//     * JWT 토큰 검증 관련 로직을 담당하는 컴포넌트의 모킹된 빈
//     */
//    @MockitoBean
//    private JwtTokenProvider jwtTokenProvider;
//
//    /**
//     * [내부 클래스 레벨]
//     * 테스트 환경에서 사용할 보안 설정을 구성
//     */
//    @TestConfiguration
//    static class TestSecurityConfig {
//        /**
//         * [메서드 레벨]
//         * CSRF를 비활성화하고 모든 요청을 허용하는 보안 설정을 구성해서 빈으로 등록
//         *
//         * @param http ServerHttpSecurity 객체
//         * @return 구성된 SecurityWebFilterChain 빈
//         */
//        @Bean
//        public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
//            return http
//                    .csrf(csrf -> csrf.disable())
//                    .authorizeExchange(exchanges -> exchanges.anyExchange().permitAll())
//                    .build();
//        }
//    }
//
//    /**
//     * [메서드 레벨]
//     * 각 테스트 실행 전에 JwtTokenProvider의 validateToken 메서드를 모킹해서,
//     * 어떤 토큰이 전달되더라도 true를 반환하도록 설정
//     */
//    @BeforeEach
//    public void setUp() {
//        when(jwtTokenProvider.validateToken(anyString())).thenReturn(true);
//    }
//
//    /**
//     * [메서드 레벨]
//     * /api/members 엔드포인트를 통해 정상적인 회원 탈퇴 기능을 테스트
//     * SecurityMockServerConfigurers.mockUser를 사용해서 인증된 사용자 정보를 SecurityContext에 주입
//     * Authorization 헤더는 이미 mockUser로 인증정보가 주입되어 있으므로 전달하지 않음
//     */
//    @Test
//    @DisplayName("정상 회원탈퇴 요청 테스트")
//    public void testWithdrawMember() {
//        Long memberId = 1L;
//        // memberWithdrawalService.withdrawMember() 메서드는 호출 시 아무 작업도 수행하지 않도록 모킹
//        doNothing().when(memberWithdrawalService).withdrawMember(memberId);
//
//        // 테스트용 사용자 정보인 CustomMemberDetails 객체를 생성
//        CustomMemberDetails customUserDetails = new CustomMemberDetails(memberId, "testUser", "password", null);
//
//        // mockUser를 사용해 인증된 사용자 정보를 SecurityContext에 주입
//        WebTestClient client = webTestClient.mutateWith(SecurityMockServerConfigurers.mockUser(customUserDetails));
//
//        client.delete()
//                .uri("/api/members")
//                // 이미 mockUser를 통해 인증정보가 주입되어 있으므로 Authorization 헤더는 제거
//                //.header("Authorization", "Bearer dummyToken")
//                .exchange()
//                // 응답 상태가 200 OK 인지 검증
//                .expectStatus().isOk()
//                // 응답 본문에서 message 필드가 "유저의 계정이 정상적으로 삭제"와 동일한지 검증
//                .expectBody()
//                .jsonPath("$.message").isEqualTo("유저의 계정이 정상적으로 삭제");
//    }
//
//    /**
//     * [메서드 레벨]
//     * 인증 정보 없이 /api/members 엔드포인트에 DELETE 요청을 보냈을 때,
//     * 400 Bad Request 상태와 적절한 에러 메시지가 반환되는지 테스트
//     */
//    @Test
//    @DisplayName("인증되지 않은 회원 탈퇴 요청 테스트")
//    public void testWithdrawMemberUnauthorized() {
//        webTestClient.delete()
//                .uri("/api/members")
//                .exchange()
//                // 응답 상태가 400 Bad Request 인지 검증
//                .expectStatus().isBadRequest()
//                // 응답 본문에서 status, errorCode, message 필드의 값이 예상대로 반환되는지 검증
//                .expectBody()
//                .jsonPath("$.status").isEqualTo(400)
//                .jsonPath("$.errorCode").isEqualTo("UNAUTHORIZED")
//                .jsonPath("$.message").isEqualTo("인증되지 않은 사용자");
//    }
}
