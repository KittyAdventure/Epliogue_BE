package com.team1.epilogue.mypage.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.team1.epilogue.auth.entity.Member;
import com.team1.epilogue.auth.repository.MemberRepository;
import com.team1.epilogue.auth.security.CustomMemberDetails;
import com.team1.epilogue.book.entity.Book;
import com.team1.epilogue.comment.entity.Comment;
import com.team1.epilogue.comment.repository.CommentRepository;
import com.team1.epilogue.mypage.dto.MyPageCalendarResponse;
import com.team1.epilogue.mypage.dto.MyPageCommentsResponse;
import com.team1.epilogue.mypage.dto.MyPageReviewsResponse;
import com.team1.epilogue.review.entity.Review;
import com.team1.epilogue.review.repository.ReviewRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

@ExtendWith(MockitoExtension.class)
class MyPageServiceTest {

  @Mock
  private ReviewRepository reviewRepository;

  @Mock
  private MemberRepository memberRepository;

  @Mock
  private CommentRepository commentRepository;

  @InjectMocks
  private MyPageService myPageService;

  private Member testMember;
  private Book testBook;
  private Review testReview;

  @BeforeEach
  void setUp() {
    testMember = Member.builder()
        .id(1L)
        .loginId("test")
        .nickname("테스트유저")
        .build();

    testBook = Book.builder()
        .id("123456789")
        .title("테스트책")
        .pubDate(LocalDate.now())
        .author("테스트작가")
        .build();

    testReview = Review.builder()
        .id(1L)
        .member(testMember)
        .book(testBook)
        .content("테스트 리뷰내용 입니다.")
        .build();
  }

  @Test
  @DisplayName("내 댓글보기 기능 테스트")
  void getMyComments() {
    //given
    // 테스트용 댓글 10개 추가
    List<Comment> commentList = new ArrayList<>();
    for (int i = 1; i <= 10; i++) {
      commentList.add(
          Comment.builder()
              .id((long) i)
              .member(testMember)
              .review(testReview)
              .content(i + "번째 테스트댓글")
              .build()
      );
    }

    // 테스트용 Page 객체 생성
    PageRequest pageRequest = PageRequest.of(0, 20);
    Page<Comment> page = new PageImpl<>(commentList, PageRequest.of(0, 20), commentList.size());

    // commentRepository.findAllByMemberId() 를 호출할때 위에서 생성한 page 객체 return
    when(commentRepository.findAllByMemberId(pageRequest, testMember)).thenReturn(page);

    //when
    MyPageCommentsResponse result = myPageService.getMyComments(CustomMemberDetails.fromMember(testMember), 1);

    //then
    assertEquals(10, result.getComments().size());
    assertEquals(1, result.getTotalPage());
    assertEquals("10번째 테스트댓글", result.getComments().get(9).getContent());
  }

  @Test
  @DisplayName("마이페이지 내부 달력 기능 테스트")
  @Disabled
  void getCalendar() {
    //given
    List<Review> reviews = new ArrayList<>();
    reviews.add(Review.builder()
        .book(testBook)
//        .createdAt(LocalDateTime.of(2025, 3, 1, 1, 1))
        .member(testMember)
        .build());
    reviews.add(Review.builder()
        .book(testBook)
//        .createdAt(LocalDateTime.of(2025, 3, 1, 1, 1))
        .member(testMember)
        .build());
    reviews.add(Review.builder()
        .book(testBook)
//        .createdAt(LocalDateTime.of(2025, 3, 5, 1, 1))
        .member(testMember)
        .build());
    reviews.add(Review.builder()
        .book(testBook)
//        .createdAt(LocalDateTime.of(2025, 3, 5, 1, 1))
        .member(testMember)
        .build());

    when(reviewRepository.findByDateAndMember(any(LocalDateTime.class), any(LocalDateTime.class),
        eq("test"))).thenReturn(reviews);

    //when
    List<MyPageCalendarResponse> responses = myPageService.getCalendar("test", "2022-02-02");

    //then
    assertEquals("2025-03-01", responses.get(0).getDate());
    assertEquals(2, responses.get(0).getCount()); // 해당 날짜에 리뷰 2개 있어야 함
    assertEquals(2, responses.size()); // 총 2개의 날짜가 있어야 함
  }

  @Test
  @DisplayName("해당 유저 리뷰 리스트 조회 기능 테스트")
  void getReviewsByMember() {
    //given
    List<Review> reviewList = new ArrayList<>();
    for (int i = 1; i <= 10; i++) {
      reviewList.add(
          Review.builder()
              .id((long) i)
              .book(testBook)
              .member(testMember) // testMember 로 10개의 리뷰 작성
              .content(i + "번째 테스트 리뷰 내용")
              .build()
      );
    }
    PageRequest pageRequest = PageRequest.of(0, 6);
    Page<Review> page = new PageImpl<>(reviewList, pageRequest, reviewList.size());

    when(memberRepository.findByLoginId("test")).thenReturn(Optional.of(testMember));
    when(reviewRepository.findByMemberId("test", pageRequest)).thenReturn(page);

    //when
    MyPageReviewsResponse reviews = myPageService.getReviewsByMember("test", 1);

    //then
    assertEquals(2, reviews.getTotalPages());
    assertEquals("테스트유저", reviews.getUserNickname());
    assertEquals("4번째 테스트 리뷰 내용", reviews.getReviews().get(3).getReviewContent());
  }
}