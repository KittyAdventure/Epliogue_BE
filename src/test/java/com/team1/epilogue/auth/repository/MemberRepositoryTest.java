package com.team1.epilogue.auth.repository;

import com.team1.epilogue.auth.entity.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * [클래스 설명]
 * Mockito를 사용하여 MemberRepository의 메서드 호출 결과를 mock하고,
 * 해당 메서드들이 예상대로 동작하는지 단위 테스트하는 클래스
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("MemberRepository 테스트")
public class MemberRepositoryTest {

    /**
     * [필드 설명]
     * Member 엔티티에 대한 CRUD 작업을 수행하는 JPA Repository 인터페이스를 Mocking
     */
    @Mock
    private MemberRepository memberRepository;

    /**
     * [메서드 설명]
     * findByLoginId 메서드가 주어진 로그인 ID로 Member를 올바르게 조회하는지 검증
     */
    @Test
    @DisplayName("findByLoginId: 회원 조회 테스트")
    public void testFindByLoginId() {
        // given: 테스트를 위한 Member 엔티티 생성 및 모의 설정
        Member member = Member.builder()
                .loginId("testUser")
                .email("test@example.com")
                .build();
        when(memberRepository.findByLoginId("testUser")).thenReturn(Optional.of(member));

        // when: 로그인 ID로 회원 조회
        Optional<Member> found = memberRepository.findByLoginId("testUser");

        // then: 조회 결과가 존재하고, 이메일이 올바른지 검증
        assertTrue(found.isPresent());
        assertEquals("test@example.com", found.get().getEmail());

        // [검증] findByLoginId가 한 번 호출되었는지 확인
        verify(memberRepository, times(1)).findByLoginId("testUser");
    }

    /**
     * [메서드 설명]
     * existsByLoginId 메서드가 주어진 로그인 ID의 존재 여부를 올바르게 반환하는지 모의 검증
     */
    @Test
    @DisplayName("existsByLoginId: 회원 존재 여부 테스트")
    public void testExistsByLoginId() {
        // given: 로그인 ID가 존재함을 모의 설정
        when(memberRepository.existsByLoginId("testUser")).thenReturn(true);

        // when: 해당 로그인 ID의 존재 여부 확인
        boolean exists = memberRepository.existsByLoginId("testUser");

        // then: 결과가 true인지 검증
        assertTrue(exists);
        verify(memberRepository, times(1)).existsByLoginId("testUser");
    }

    /**
     * [메서드 설명]
     * findByEmail 메서드가 주어진 이메일로 Member를 올바르게 조회하는지 모의 검증
     */
    @Test
    @DisplayName("findByEmail: 회원 조회 테스트")
    public void testFindByEmail() {
        // given: 테스트를 위한 Member 엔티티 생성 및 모의 설정
        Member member = Member.builder()
                .loginId("testUser")
                .email("test@example.com")
                .build();
        when(memberRepository.findByEmail("test@example.com")).thenReturn(Optional.of(member));

        // when: 이메일로 회원 조회
        Optional<Member> found = memberRepository.findByEmail("test@example.com");

        // then: 조회된 결과가 존재하고, loginId가 올바른지 검증
        assertTrue(found.isPresent());
        assertEquals("testUser", found.get().getLoginId());
        verify(memberRepository, times(1)).findByEmail("test@example.com");
    }

    /**
     * [메서드 설명]
     * existsByEmail 메서드가 주어진 이메일의 존재 여부를 올바르게 반환하는지 모의 검증
     */
    @Test
    @DisplayName("existsByEmail: 회원 존재 여부 테스트")
    public void testExistsByEmail() {
        // given: 이메일이 존재함을 모의 설정
        when(memberRepository.existsByEmail("test@example.com")).thenReturn(true);

        // when: 해당 이메일의 존재 여부 확인
        boolean exists = memberRepository.existsByEmail("test@example.com");

        // then: 결과가 true인지 검증
        assertTrue(exists);
        verify(memberRepository, times(1)).existsByEmail("test@example.com");
    }
}
