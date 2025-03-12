package com.team1.epilogue.follow.service;

import com.team1.epilogue.auth.entity.Member;
import com.team1.epilogue.auth.repository.MemberRepository;
import com.team1.epilogue.book.entity.Book;
import com.team1.epilogue.book.repository.BookRepository;
import com.team1.epilogue.follow.dto.FollowActionResponse;
import com.team1.epilogue.follow.dto.MemberDto;
import com.team1.epilogue.follow.dto.ReviewDto;
import com.team1.epilogue.follow.dto.ReviewListResponse;
import com.team1.epilogue.follow.entity.Follow;
import com.team1.epilogue.follow.repository.FollowRepository;
import com.team1.epilogue.review.entity.Review;
import com.team1.epilogue.review.repository.ReviewRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@DisplayName("FollowService 통합 테스트")
class FollowServiceTest {

    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private FollowRepository followRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private FollowService followService;

    private Member follower;
    private Member followed;

    @BeforeEach
    void setUp() {
        memberRepository.deleteAll();
        followRepository.deleteAll();
        reviewRepository.deleteAll();

        // 테스트용 회원 데이터 생성
        follower = Member.builder()
                .loginId("user1")
                .password("password1")
                .name("User One")
                .nickname("User One")
                .profileUrl("http://example.com/user1.png")
                .birthDate(LocalDate.of(1990, 1, 1))
                .email("user12@example.com")
                .phone("010-1234-5678")
                .point(0)
                .build();

        followed = Member.builder()
                .loginId("user2")
                .password("password2")
                .name("User Two")
                .nickname("User Two")
                .profileUrl("http://example.com/user2.png")
                .birthDate(LocalDate.of(1992, 2, 2))
                .email("user22@example.com")
                .phone("010-8765-4321")
                .point(0)
                .build();

        memberRepository.save(follower);
        memberRepository.save(followed);
    }

    @Test
    @DisplayName("팔로우 생성 성공 테스트")
    void testFollowUser_success() {
        FollowActionResponse response = followService.followUser("user1", "user2");
        assertNotNull(response);
        assertEquals("Follow creation successful", response.getMessage());
        assertEquals("user1", response.getFollowerLoginId());
        assertEquals("user2", response.getFollowedLoginId());
    }

    @Test
    @DisplayName("이미 팔로우 중인 경우 예외 발생 테스트")
    void testFollowUser_alreadyFollowing() {
        followService.followUser("user1", "user2");
        Exception exception = assertThrows(ResponseStatusException.class, () -> {
            followService.followUser("user1", "user2");
        });
        assertTrue(exception.getMessage().contains("Already following"));
    }

    @Test
    @DisplayName("팔로우 삭제 성공 테스트")
    void testUnfollowUser_success() {
        followService.followUser("user1", "user2");
        followService.unfollowUser("user1", "user2");
        List<Follow> follows = followRepository.findByFollower(follower);
        assertTrue(follows.isEmpty());
    }

    @Test
    @DisplayName("팔로잉 목록 조회 성공 테스트")
    void testGetFollowingList_success() {
        followService.followUser("user1", "user2");
        List<MemberDto> followingList = followService.getFollowingList("user1");
        assertEquals(1, followingList.size());
        MemberDto dto = followingList.get(0);
        assertEquals("user2", dto.getLoginId());
    }

    @Test
    @DisplayName("팔로워 목록 조회 성공 테스트 (Fetch Join 적용)")
    void testGetFollowersList_success() {
        followService.followUser("user1", "user2");
        List<MemberDto> followersList = followService.getFollowersList("user2");
        assertEquals(1, followersList.size());
        MemberDto dto = followersList.get(0);
        assertEquals("user1", dto.getLoginId());
    }

    @Test
    @DisplayName("팔로우한 회원의 리뷰 조회 성공 테스트")
    void testGetFollowedReviews_success() {
        followService.followUser("user1", "user2");

        Book testBook = Book.builder()
                .id("1")
                .title("Test Book")
                .build();

     bookRepository.save(testBook);

        Review review = Review.builder()
                .member(followed)
                .book(testBook)
                .content("Test review content")
                .imageUrl("http://example.com/image.png")
//                .createdAt(LocalDateTime.now())
                .build();
        reviewRepository.save(review);

        ReviewListResponse response = followService.getFollowedReviews("user1", 1, 10, "desc");
        assertNotNull(response);
        assertEquals(1, response.getPagination().getTotal());
        assertFalse(response.getReview().isEmpty());
        ReviewDto dto = response.getReview().get(0);
        assertEquals("Test review content", dto.getContent());
    }
}