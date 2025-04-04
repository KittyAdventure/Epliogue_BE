package com.team1.epilogue.follow.service;

import com.team1.epilogue.auth.entity.Member;
import com.team1.epilogue.auth.repository.MemberRepository;
import com.team1.epilogue.book.entity.Book;
import com.team1.epilogue.book.repository.BookRepository;
import com.team1.epilogue.follow.dto.FollowActionResponse;
import com.team1.epilogue.follow.dto.MemberDto;
import com.team1.epilogue.follow.dto.PaginationDto;
import com.team1.epilogue.follow.dto.ReviewDto;
import com.team1.epilogue.follow.dto.ReviewListResponse;
import com.team1.epilogue.follow.entity.Follow;
import com.team1.epilogue.follow.repository.FollowRepository;
import com.team1.epilogue.review.entity.Review;
import com.team1.epilogue.review.repository.ReviewRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("FollowService 단위 테스트")
class FollowServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private FollowRepository followRepository;

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private FollowService followService;

    private Member follower;
    private Member followed;

    @BeforeEach
    void setUp() {
        follower = Member.builder()
                .id(1L)
                .loginId("user1")
                .password("password1")
                .name("User One")
                .nickname("User One")
                .profileUrl("http://example.com/user1.png")
                .birthDate(LocalDate.of(1990, 1, 1))
                .email("user1@example.com")
                .phone("010-1234-5678")
                .build();

        followed = Member.builder()
                .id(2L)
                .loginId("user2")
                .password("password2")
                .name("User Two")
                .nickname("User Two")
                .profileUrl("http://example.com/user2.png")
                .birthDate(LocalDate.of(1992, 2, 2))
                .email("user2@example.com")
                .phone("010-8765-4321")
                .build();
    }

    @Test
    @DisplayName("팔로우 생성 성공 테스트")
    void testFollowUser_success() {
        when(memberRepository.findByLoginId("user1")).thenReturn(Optional.of(follower));
        when(memberRepository.findByLoginId("user2")).thenReturn(Optional.of(followed));
        when(followRepository.findByFollowerAndFollowed(follower, followed)).thenReturn(Optional.empty());

        Follow savedFollow = new Follow();
        savedFollow.setFollower(follower);
        savedFollow.setFollowed(followed);
        when(followRepository.save(any(Follow.class))).thenReturn(savedFollow);

        FollowActionResponse response = followService.followUser("user1", "user2");
        assertNotNull(response);
        assertEquals("Follow creation successful", response.getMessage());
        assertEquals("user1", response.getFollowerLoginId());
        assertEquals("user2", response.getFollowedLoginId());
    }

    @Test
    @DisplayName("이미 팔로우 중인 경우 예외 발생 테스트")
    void testFollowUser_alreadyFollowing() {
        when(memberRepository.findByLoginId("user1")).thenReturn(Optional.of(follower));
        when(memberRepository.findByLoginId("user2")).thenReturn(Optional.of(followed));
        Follow existingFollow = new Follow();
        existingFollow.setFollower(follower);
        existingFollow.setFollowed(followed);
        when(followRepository.findByFollowerAndFollowed(follower, followed))
                .thenReturn(Optional.empty())
                .thenReturn(Optional.of(existingFollow));

        followService.followUser("user1", "user2");
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            followService.followUser("user1", "user2");
        });
        assertTrue(exception.getMessage().contains("Already following"));
    }

    @Test
    @DisplayName("팔로우 삭제 성공 테스트")
    void testUnfollowUser_success() {
        when(memberRepository.findByLoginId("user1")).thenReturn(Optional.of(follower));
        when(memberRepository.findByLoginId("user2")).thenReturn(Optional.of(followed));
        Follow follow = new Follow();
        follow.setFollower(follower);
        follow.setFollowed(followed);
        when(followRepository.findByFollowerAndFollowed(follower, followed)).thenReturn(Optional.of(follow));

        assertDoesNotThrow(() -> followService.unfollowUser("user1", "user2"));
    }

    @Test
    @DisplayName("팔로잉 목록 조회 성공 테스트")
    void testGetFollowingList_success() {
        when(memberRepository.findByLoginId("user1")).thenReturn(Optional.of(follower));
        Follow follow = new Follow();
        follow.setFollowed(followed);
        when(followRepository.findByFollowerWithFollowed(follower)).thenReturn(List.of(follow));

        List<MemberDto> followingList = followService.getFollowingList("user1");
        assertEquals(1, followingList.size());
        MemberDto dto = followingList.get(0);
        assertEquals(String.valueOf(followed.getId()), dto.getId());
        assertEquals("user2", dto.getLoginId());
    }

    @Test
    @DisplayName("팔로워 목록 조회 성공 테스트")
    void testGetFollowersList_success() {
        when(memberRepository.findByLoginId("user2")).thenReturn(Optional.of(followed));
        Follow follow = new Follow();
        follow.setFollower(follower);
        when(followRepository.findByFollowedWithFollower(followed)).thenReturn(List.of(follow));

        List<MemberDto> followersList = followService.getFollowersList("user2");
        assertEquals(1, followersList.size());
        MemberDto dto = followersList.get(0);
        assertEquals(String.valueOf(follower.getId()), dto.getId());
        assertEquals("user1", dto.getLoginId());
    }

    @Test
    @DisplayName("팔로우한 회원의 리뷰 조회 성공 테스트")
    void testGetFollowedReviews_success() {
        when(memberRepository.findByLoginId("user1")).thenReturn(Optional.of(follower));
        Follow follow = new Follow();
        follow.setFollower(follower);
        follow.setFollowed(followed);
        when(followRepository.findByFollower(follower)).thenReturn(List.of(follow));

        Review review = Review.builder()
                .member(followed)
                // Book 정보는 reviewRepository.findByMemberInWithFetchJoin의 파라미터로 처리됨
                .content("Test review content")
                .build();

        Pageable pageable = PageRequest.of(0, 10, Sort.by("createdAt").descending());
        Page<Review> reviewPage = new PageImpl<>(List.of(review), pageable, 1);
        when(reviewRepository.findByMemberInWithFetchJoin(List.of(followed), pageable)).thenReturn(reviewPage);

        ReviewListResponse response = followService.getFollowedReviews("user1", 1, 10, "desc");
        assertNotNull(response);
        assertEquals(1, response.getPagination().getTotal());
        assertFalse(response.getReview().isEmpty());
        ReviewDto dto = response.getReview().get(0);
        assertEquals("Test review content", dto.getContent());
    }
}