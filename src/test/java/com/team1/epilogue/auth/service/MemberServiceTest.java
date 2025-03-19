package com.team1.epilogue.auth.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.team1.epilogue.auth.dto.*;
import com.team1.epilogue.auth.entity.Member;
import com.team1.epilogue.auth.exception.*;
import com.team1.epilogue.auth.repository.CustomMemberRepository;
import com.team1.epilogue.auth.repository.MemberRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@Slf4j
@ExtendWith(MockitoExtension.class)
@DisplayName("MemberService 테스트")
public class MemberServiceTest {

    ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private CustomMemberRepository customMemberRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private MemberService memberService;

    private RegisterRequest registerRequest;

    @BeforeEach
    public void setup() {
        registerRequest = new RegisterRequest();
        registerRequest.setLoginId("serviceMember");
        registerRequest.setPassword("password123");
        registerRequest.setNickname("serviceNick");
        registerRequest.setName("Service Member");
        registerRequest.setBirthDate("1990-01-01");
        registerRequest.setEmail("service@example.com");
        registerRequest.setPhone("010-1234-5678");
        registerRequest.setProfileUrl("http://example.com/photo.jpg");
    }

    @Test
    @DisplayName("정상 회원 등록 테스트")
    public void testRegisterMemberSuccess() {
        when(memberRepository.existsByLoginId("serviceMember")).thenReturn(false);
        when(memberRepository.existsByEmail("service@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword123");
        when(memberRepository.save(any(Member.class))).thenReturn(Member.builder()
                .id(1L)
                .loginId(registerRequest.getLoginId())
                .password("encodedPassword123")
                .nickname(registerRequest.getNickname())
                .name(registerRequest.getName())
                .birthDate(LocalDate.parse(registerRequest.getBirthDate()))
                .email(registerRequest.getEmail())
                .phone(registerRequest.getPhone())
                .profileUrl(registerRequest.getProfileUrl())
                .point(0)
                .social(null)
                .build()
        );

        MemberResponse response = memberService.registerMember(registerRequest, null);

        assertNotNull(response);
        assertEquals("serviceMember", response.getLoginId());
        verify(memberRepository, times(1)).save(any(Member.class));
    }

    @Test
    @DisplayName("중복 로그인 ID 테스트")
    public void testDuplicateMemberId() {
        when(memberRepository.existsByLoginId("serviceMember")).thenReturn(true);

        assertThrows(IdAlreadyExistException.class, () -> memberService.registerMember(registerRequest, null));
    }

    @Test
    @DisplayName("중복 이메일 테스트")
    public void testDuplicateEmail() {
        when(memberRepository.existsByLoginId("serviceMember")).thenReturn(false);
        when(memberRepository.existsByEmail("service@example.com")).thenReturn(true);

        assertThrows(EmailAlreadyExistException.class, () -> memberService.registerMember(registerRequest, null));
    }

    @Test
    @DisplayName("정상 회원 정보 수정 테스트")
    public void testUpdateMemberSuccess() {
        Member existingMember = Member.builder()
                .id(1L)
                .loginId("serviceMember")
                .password("encodedPassword123")
                .nickname("serviceNick")
                .name("Service Member")
                .birthDate(LocalDate.parse("1990-01-01"))
                .email("service@example.com")
                .phone("010-1234-5678")
                .profileUrl("http://example.com/photo.jpg")
                .point(0)
                .social(null)
                .build();

        UpdateMemberRequest updateRequest = new UpdateMemberRequest();
        updateRequest.setNickname("updatedNick");
        updateRequest.setEmail("updated@example.com");
        updateRequest.setPhone("010-0000-0000");
        updateRequest.setProfilePhoto("http://example.com/newphoto.jpg");

        when(memberRepository.findById(1L)).thenReturn(Optional.of(existingMember));
        when(memberRepository.save(any(Member.class))).thenReturn(existingMember);

        MemberResponse response = memberService.updateMember(1L, updateRequest, null);

        assertNotNull(response);
        assertEquals("updatedNick", response.getNickname());
        assertEquals("updated@example.com", response.getEmail());
    }

    @Test
    @DisplayName("회원 정보 수정 - 회원이 존재하지 않을 경우")
    public void testUpdateMemberNotFound() {
        UpdateMemberRequest updateRequest = new UpdateMemberRequest();
        updateRequest.setNickname("updatedNick");
        updateRequest.setEmail("updated@example.com");

        when(memberRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(MemberNotFoundException.class, () -> memberService.updateMember(1L, updateRequest, null));
    }


    @Test
    @DisplayName("회원 아이디로 검색 - LoginId 검색")
    void searchLoginTest() throws JsonProcessingException {
        //Given
        Pageable pageable = PageRequest.of(0,9, Sort.by("id").ascending());
        String sortType = "oldest"; // 오래된 순

        List<Member> mockMembers = List.of(
            new Member(1L, "testUser", "1234", "Tester", "Test", null, null, null, null, 0, null, null),
            new Member(2L, "anotherTest", "5678", "AnotherTester", "Another", null, null, null, null, 0, null, null),
            new Member(3L, "testAccount", "abcd", "TestNick", "TestName", null, null, null, null, 0, null, null),
            new Member(4L, "user123", "pass123", "Nick1", "User One", null, null, null, null, 0, null, null),
            new Member(5L, "superTest", "pass456", "Nick2", "User Two", null, null, null, null, 0, null, null),
            new Member(6L, "helloTest", "pass789", "Nick3", "User Three", null, null, null, null, 0, null, null),
            new Member(7L, "tester2024", "pass000", "Nick4", "User Four", null, null, null, null, 0, null, null),
            new Member(8L, "exampleTest", "pass111", "Nick5", "User Five", null, null, null, null, 0, null, null),
            new Member(9L, "finalTestUser", "pass222", "Nick6", "User Six", null, null, null, null, 0, null, null),
            new Member(10L, "ultimateTester", "pass333", "Nick7", "User Seven", null, null, null, null, 0, null, null)
        );

        // "test"가 포함된 데이터만 필터링
        List<Member> filteredMembers = mockMembers.stream()
            .filter(member -> member.getLoginId().toLowerCase().contains("test"))
            .toList(); // 9개만 남음!
        log.info("필터링된 회원 수: " + objectMapper.writeValueAsString(filteredMembers));
        filteredMembers.forEach(m -> log.info(m.getLoginId()));

        Page<Member> mockPage = new PageImpl<>(filteredMembers, pageable, filteredMembers.size());

        when(customMemberRepository.searchMembers("loginId", "test", pageable,null,sortType)).thenReturn(mockPage);
        //when
        Page<SearchMemberResponseDto> result = memberService.searchMember("loginId", "test", pageable,null,sortType);
        log.info("검색된 회원 개수: " + objectMapper.writeValueAsString(result.getContent().size()));
        result.getContent().forEach(m -> {
          try {
            log.info(objectMapper.writeValueAsString(m.getLoginId()));
          } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
          }
        });
        //Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(9);
        assertThat(result.getTotalElements()).isEqualTo(9);
        verify(customMemberRepository, times(1)).searchMembers("loginId", "test", pageable,null,sortType);
    }


    @Test
    @DisplayName("회원 닉네임으로 검색 - Nick 검색")
    void searchNicknameTest() throws JsonProcessingException {
        //Given
        Pageable pageable = PageRequest.of(0,9);
        String sortType = "newest"; // 최신

        List<Member> mockMembers = List.of(
            new Member(1L, "testUser", "1234", "Tester", "Test", null, "TESTER@gmail.com", null, null, 0, null, null),
            new Member(2L, "anotherTest", "5678", "AnotherTester", "Another", null, "testga@gmail.com", null, null, 0, null, null),
            new Member(3L, "testAccount", "abcd", "TestNick", "TestName", null, "ttttas@gmail.com", null, null, 0, null, null),
            new Member(4L, "user123", "pass123", "Nick1", "User One", null, "tasttt@gmail.com", null, null, 0, null, null),
            new Member(5L, "superTest", "pass456", "Nick2", "User Two", null, "tast234@gmail.com", null, null, 0, null, null),
            new Member(6L, "helloTest", "pass789", "Nick3", "User Three", null, "zzang@gmail.com", null, null, 0, null, null),
            new Member(7L, "tester2024", "pass000", "Nick4", "User Four", null, "CCOng@gmail.com", null, null, 0, null, null),
            new Member(8L, "exampleTest", "pass111", "Nick5", "User Five", null, "RURU@gmail.com", null, null, 0, null, null),
            new Member(9L, "finalTestUser", "pass222", "Nick6", "User Six", null, "test5566@gmail.com", null, null, 0, null, null),
            new Member(10L, "ultimateTester", "pass333", "Nick7", "User Seven", null, "gametest@gmail.com", null, null, 0, null, null)
        );

        // "nick"가 포함된 데이터만 필터링
        List<Member> filteredMembers = mockMembers.stream()
            .filter(member -> member.getNickname().toLowerCase().contains("nick"))
            .toList();
        log.info("필터링된 회원 수: " + objectMapper.writeValueAsString(filteredMembers));
        filteredMembers.forEach(m -> log.info(m.getNickname()));

        Page<Member> mockPage = new PageImpl<>(filteredMembers, pageable, filteredMembers.size());

        when(customMemberRepository.searchMembers("nickname", "nick", pageable,null,sortType)).thenReturn(mockPage);
        //when
        Page<SearchMemberResponseDto> result = memberService.searchMember("nickname", "nick", pageable,null,sortType);
        log.info("검색된 닉네임 개수: " + objectMapper.writeValueAsString(result.getContent().size()));
        result.getContent().forEach(m -> {
            try {
                log.info(objectMapper.writeValueAsString(m.getNickname()));
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        });
        //Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(8);
        assertThat(result.getTotalElements()).isEqualTo(8);
        verify(customMemberRepository, times(1)).searchMembers("nickname", "nick", pageable,null,sortType);
    }


    @Test
    @DisplayName("회원 이메일으로 검색 - email 검색")
    void searchEmailTest() throws JsonProcessingException {
        //Given
        Pageable pageable = PageRequest.of(0,9, Sort.by("id").ascending());
        //String sortType = ""; // 기본 정렬
        List<Member> mockMembers = List.of(
            new Member(1L, "testUser", "1234", "Tester", "Test", null, "TESTER@gmail.com", null, null, 0, null, null),
            new Member(2L, "anotherTest", "5678", "AnotherTester", "Another", null, "testga@gmail.com", null, null, 0, null, null),
            new Member(3L, "testAccount", "abcd", "TestNick", "TestName", null, "ttttas@gmail.com", null, null, 0, null, null),
            new Member(4L, "user123", "pass123", "Nick1", "User One", null, "tasttt@gmail.com", null, null, 0, null, null),
            new Member(5L, "superTest", "pass456", "Nick2", "User Two", null, "tast234@gmail.com", null, null, 0, null, null),
            new Member(6L, "helloTest", "pass789", "Nick3", "User Three", null, "zzang@gmail.com", null, null, 0, null, null),
            new Member(7L, "tester2024", "pass000", "Nick4", "User Four", null, "CCOng@gmail.com", null, null, 0, null, null),
            new Member(8L, "exampleTest", "pass111", "Nick5", "User Five", null, "RURU@gmail.com", null, null, 0, null, null),
            new Member(9L, "finalTestUser", "pass222", "Nick6", "User Six", null, "test5566@gmail.com", null, null, 0, null, null),
            new Member(10L, "ultimateTester", "pass333", "Nick7", "User Seven", null, "gametest@gmail.com", null, null, 0, null, null)
        );

        // "nick"가 포함된 데이터만 필터링
        List<Member> filteredMembers = mockMembers.stream()
            .filter(member -> member.getEmail().toLowerCase().contains("test"))
            .toList();
        log.info("필터링된 이메일 수: " + objectMapper.writeValueAsString(filteredMembers));
        filteredMembers.forEach(m -> log.info(m.getEmail()));

        Page<Member> mockPage = new PageImpl<>(filteredMembers, pageable, filteredMembers.size());

        when(customMemberRepository.searchMembers("email", "test", pageable,null,"")).thenReturn(mockPage);
        //when
        Page<SearchMemberResponseDto> result = memberService.searchMember("email", "test", pageable,null,"");
        log.info("검색된 email 개수: " + objectMapper.writeValueAsString(result.getContent().size()));
        result.getContent().forEach(m -> {
            try {
                log.info(objectMapper.writeValueAsString(m.getEmail()));
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        });
        //Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(4);
        assertThat(result.getTotalElements()).isEqualTo(4);
        verify(customMemberRepository, times(1)).searchMembers("email", "test", pageable,null,"");
    }


}
