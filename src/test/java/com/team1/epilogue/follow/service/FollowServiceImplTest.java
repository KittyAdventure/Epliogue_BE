package com.team1.epilogue.follow.service;

import com.team1.epilogue.auth.entity.Member;
import com.team1.epilogue.auth.repository.MemberRepository;
import com.team1.epilogue.book.entity.Book;
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
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.*;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("FollowServiceImpl 테스트")
class FollowServiceImplTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private FollowRepository followRepository;

    @Mock
    private ReviewRepository reviewRepository;

    @InjectMocks
    private FollowServiceImpl followService;

    private Member follower;
    private Member followed;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        follower = Member.builder()
                .id(1L)
                .loginId("user1")
                .nickname("User One")
                .profileUrl("http://example.com/user1.png")
                .build();

        followed = Member.builder()
                .id(2L)
                .loginId("user2")
                .nickname("User Two")
                .profileUrl("http://example.com/user2.png")
                .build();
    }

    @Test
    @DisplayName("팔로우 생성 성공 테스트")
    void testFollowUser_success() {
        when(memberRepository.findByLoginId("user1")).thenReturn(Optional.of(follower));
        when(memberRepository.findByLoginId("user2")).thenReturn(Optional.of(followed));
        when(followRepository.findByFollowerAndFollowed(follower, followed)).thenReturn(Optional.empty());

        FollowActionResponse response = followService.followUser("user1", "user2");

        assertNotNull(response);
        assertEquals("Follow creation successful", response.getMessage());
        assertEquals("user1", response.getFollowerLoginId());
        assertEquals("user2", response.getFollowedLoginId());
        ArgumentCaptor<Follow> followCaptor = ArgumentCaptor.forClass(Follow.class);
        verify(followRepository).save(followCaptor.capture());
        Follow savedFollow = followCaptor.getValue();
        assertEquals(follower, savedFollow.getFollower());
        assertEquals(followed, savedFollow.getFollowed());
    }

    @Test
    @DisplayName("이미 팔로우 중인 경우 예외 발생 테스트")
    void testFollowUser_alreadyFollowing() {
        when(memberRepository.findByLoginId("user1")).thenReturn(Optional.of(follower));
        when(memberRepository.findByLoginId("user2")).thenReturn(Optional.of(followed));
        Follow existingFollow = Follow.builder().id(100L).follower(follower).followed(followed).build();
        when(followRepository.findByFollowerAndFollowed(follower, followed)).thenReturn(Optional.of(existingFollow));

        Exception exception = assertThrows(RuntimeException.class, () -> {
            followService.followUser("user1", "user2");
        });
        assertTrue(exception.getMessage().contains("Already following"));
    }

    @Test
    @DisplayName("팔로우 삭제 성공 테스트")
    void testUnfollowUser_success() {
        when(memberRepository.findByLoginId("user1")).thenReturn(Optional.of(follower));
        when(memberRepository.findByLoginId("user2")).thenReturn(Optional.of(followed));
        Follow follow = Follow.builder().id(100L).follower(follower).followed(followed).build();
        when(followRepository.findByFollowerAndFollowed(follower, followed)).thenReturn(Optional.of(follow));

        followService.unfollowUser("user1", "user2");

        verify(followRepository).delete(follow);
    }

    @Test
    @DisplayName("팔로잉 목록 조회 성공 테스트")
    void testGetFollowingList_success() {
        when(memberRepository.findByLoginId("user1")).thenReturn(Optional.of(follower));
        Follow follow1 = Follow.builder().id(1L).follower(follower).followed(followed).build();
        when(followRepository.findByFollower(follower)).thenReturn(List.of(follow1));

        List<MemberDto> followingList = followService.getFollowingList("user1");

        assertEquals(1, followingList.size());
        MemberDto dto = followingList.get(0);
        assertEquals("user2", dto.getLoginId());
    }

    @Test
    @DisplayName("팔로워 목록 조회 성공 테스트")
    void testGetFollowersList_success() {
        when(memberRepository.findByLoginId("user2")).thenReturn(Optional.of(followed));
        Follow follow1 = Follow.builder().id(1L).follower(follower).followed(followed).build();
        when(followRepository.findByFollowed(followed)).thenReturn(List.of(follow1));

        List<MemberDto> followersList = followService.getFollowersList("user2");

        assertEquals(1, followersList.size());
        MemberDto dto = followersList.get(0);
        assertEquals("user1", dto.getLoginId());
    }

    @Test
    @DisplayName("팔로우한 회원이 없을 경우 리뷰 조회 테스트")
    void testGetFollowedReviews_noFollowedMembers() {
        when(memberRepository.findByLoginId("user1")).thenReturn(Optional.of(follower));
        when(followRepository.findByFollower(follower)).thenReturn(Collections.emptyList());

        ReviewListResponse response = followService.getFollowedReviews("user1", 1, 10, "desc");

        assertNotNull(response);
        assertEquals(0, response.getPagination().getTotal());
        assertTrue(response.getReview().isEmpty());
    }

    @Test
    @DisplayName("팔로우한 회원의 리뷰 조회 성공 테스트")
    void testGetFollowedReviews_success() {
        when(memberRepository.findByLoginId("user1")).thenReturn(Optional.of(follower));
        Follow follow1 = Follow.builder().id(1L).follower(follower).followed(followed).build();
        when(followRepository.findByFollower(follower)).thenReturn(List.of(follow1));

        Book testDummyBook = Book.builder().id("B1")
                .title("Book Title")
                .author("Author Name")
                .description("Description")
                .avgRating(4.5)
                .coverUrl("http://example.com/cover.png")
                .build();

        Review review = Review.builder()
                .id(100L)
                .member(followed)
                .book(testDummyBook)
                .content("Review content")
                .imageUrl("http://example.com/image.png")
                .build();

        Pageable pageable = PageRequest.of(0, 10, Sort.by("createdAt").descending());
        Page<Review> reviewPage = new PageImpl<>(List.of(review), pageable, 1);
        when(reviewRepository.findByMemberIn(List.of(followed), pageable)).thenReturn(reviewPage);

        ReviewListResponse response = followService.getFollowedReviews("user1", 1, 10, "desc");

        assertNotNull(response);
        assertEquals(1, response.getPagination().getTotal());
        assertFalse(response.getReview().isEmpty());
        ReviewDto dto = response.getReview().get(0);
        assertEquals("Review content", dto.getContent());
    }
}
