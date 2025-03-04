package com.team1.epilogue.comment.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.team1.epilogue.auth.entity.Member;
import com.team1.epilogue.book.entity.Book;
import com.team1.epilogue.comment.dto.CommentPostRequest;
import com.team1.epilogue.comment.entity.Comment;
import com.team1.epilogue.comment.repository.CommentRepository;
import com.team1.epilogue.review.entity.Review;
import com.team1.epilogue.review.repository.ReviewRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

  @Mock
  private CommentRepository commentRepository;

  @Mock
  private ReviewRepository reviewRepository;

  @InjectMocks
  private CommentService commentService;

  private Member member;
  private Review review;
  private Book book;

  @BeforeEach
  void setUp() {
    member = Member.builder()
        .id(1L)
        .loginId("test1")
        .name("수빈")
        .build();

    book = Book.builder()
        .id("123")
        .title("어린왕자")
        .author("생텍쥐페리")
        .build();
  }

  @Test
  @DisplayName("댓글 작성 기능 테스트")
  void postComment() {
    // given
    review = Review.builder()
        .id(1L)
        .member(member)
        .content("테스트 리뷰 내용입니다.")
        .book(book)
        .build();

    when(reviewRepository.findById(1L)).thenReturn(Optional.of(review));

    CommentPostRequest testRequest = CommentPostRequest.builder()
        .memberId("수빈")
        .content("테스트 댓글 내용입니다.") // 기존에 리뷰 내용과 동일했는데 댓글 내용으로 수정
        .reviewId(1L)
        .build();

    Comment comment = Comment.builder()
        .id(1L)
        .member(member)
        .review(review)
        .content("테스트 댓글 내용입니다.") // 실제 저장될 댓글 내용 추가
        .build();

    // ArgumentCaptor 생성
    ArgumentCaptor<Comment> commentCaptor = ArgumentCaptor.forClass(Comment.class);
    when(commentRepository.save(commentCaptor.capture())).thenReturn(comment);

    // when
    Comment response = commentService.postComment(member, testRequest);

    // then
    assertNotNull(response);
    assertEquals(member.getId(), response.getMember().getId());
    assertEquals(review.getId(), response.getReview().getId());
    assertEquals("테스트 댓글 내용입니다.", response.getContent());

    // 캡처된 객체 확인
    Comment capturedComment = commentCaptor.getValue();
    assertEquals(member.getId(), capturedComment.getMember().getId());
    assertEquals(review.getId(), capturedComment.getReview().getId());
    assertEquals("테스트 댓글 내용입니다.", capturedComment.getContent());
  }
}