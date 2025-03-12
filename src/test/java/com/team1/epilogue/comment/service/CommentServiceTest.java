package com.team1.epilogue.comment.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.team1.epilogue.auth.entity.Member;
import com.team1.epilogue.auth.security.CustomMemberDetails;
import com.team1.epilogue.book.entity.Book;
import com.team1.epilogue.comment.dto.CommentPostRequest;
import com.team1.epilogue.comment.dto.CommentResponse;
import com.team1.epilogue.comment.dto.CommentUpdateRequest;
import com.team1.epilogue.comment.entity.Comment;
import com.team1.epilogue.comment.entity.CommentLike;
import com.team1.epilogue.comment.exception.CommentNotFoundException;
import com.team1.epilogue.comment.exception.UnauthorizedMemberException;
import com.team1.epilogue.comment.repository.CommentLikeRepository;
import com.team1.epilogue.comment.repository.CommentRepository;
import com.team1.epilogue.review.entity.Review;
import com.team1.epilogue.review.exception.AlreadyLikedException;
import com.team1.epilogue.review.exception.LikeNotFoundException;
import com.team1.epilogue.review.repository.ReviewRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

  @Mock
  private CommentRepository commentRepository;

  @Mock
  private ReviewRepository reviewRepository;

  @Mock
  private CommentLikeRepository commentLikeRepository;

  @InjectMocks
  private CommentService commentService;

  private Member member;
  private Review review;
  private Book book;
  private Comment comment;
  private CustomMemberDetails memberDetails;

  @BeforeEach
  void setUp() {
    member = Member.builder()
        .id(1L)
        .loginId("test1")
        .nickname("수빈1")
        .name("수빈")
        .build();

    book = Book.builder()
        .id("123")
        .title("어린왕자")
        .author("생텍쥐페리")
        .build();

    review = Review.builder()
        .id(1L)
        .build();

    comment = Comment.builder()
            .id(1L)
            .member(member)
            .content("테스트 댓글")
            .likeCount(0)
            .build();

    memberDetails = new CustomMemberDetails(
            member,
            member.getId(),
            member.getLoginId(),
            member.getPassword(),
            Collections.emptyList(),
            member.getName(),
            member.getEmail()
    );
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
        .content("테스트 댓글 내용입니다.")
        .reviewId(1L)
        .build();

    Comment comment = Comment.builder()
        .id(1L)
        .member(member)
        .review(review)
        .content("테스트 댓글 내용입니다.")
        .build();

    // ArgumentCaptor 생성
    ArgumentCaptor<Comment> commentCaptor = ArgumentCaptor.forClass(Comment.class);
    when(commentRepository.save(commentCaptor.capture())).thenReturn(comment);

    // when
    Comment response = commentService.postComment(CustomMemberDetails.fromMember(member), testRequest);

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

  @Test
  @DisplayName("댓글 수정 기능 테스트")
  void updateComment() {
    // given
    Member anotherMember = Member.builder()
        .loginId("anotherUser") // 다른 사용자 ID
        .build();

    Comment comment = Comment.builder()
        .id(1L)
        .content("테스트1")
        .member(member)
        .build();

    CommentUpdateRequest request = CommentUpdateRequest.builder()
        .commentId(1L)
        .content("테스트2")
        .build();

    when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));

    // ArgumentCaptor 생성
    ArgumentCaptor<Comment> commentCaptor = ArgumentCaptor.forClass(Comment.class);
    when(commentRepository.save(commentCaptor.capture())).thenReturn(comment);

    // when
    Comment response = commentService.updateComment(CustomMemberDetails.fromMember(member), request);

    // then
    assertNotNull(response);
    assertEquals("테스트1", response.getContent());
    assertThrows(UnauthorizedMemberException.class, () -> {
      commentService.updateComment(CustomMemberDetails.fromMember(anotherMember), request);
    });
  }

  @Test
  @DisplayName("댓글 삭제기능 테스트")
  void deleteComment() {
    //given
    Long commentId = 1L;
    Comment comment = Comment.builder()
        .id(1L)
        .member(member)
        .content("테스트 댓글입니다.")
        .review(review)
        .build();
    when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));

    // when
    assertDoesNotThrow(() -> commentService.deleteComment(CustomMemberDetails.fromMember(member), commentId));

    //then
    verify(commentRepository, times(1)).delete(comment);
  }

  @DisplayName("댓글 삭제기능 실패 테스트 - 존재하지 않는 댓글")
  @Test
  void deleteComment_fail_commentNotFound() {
    // given
    when(commentRepository.findById(any(Long.class))).thenReturn(Optional.empty());

    // when & then
    assertThrows(CommentNotFoundException.class,
        () -> commentService.deleteComment(CustomMemberDetails.fromMember(member), 1L));

  }

  @DisplayName("댓글 삭제기능 실패 테스트 - 본인의 댓글이 아닐때")
  @Test
  void deleteComment_fail_UnAuthorized() {
    // given
    Long commentId = 1L;
    Member member2 = Member
        .builder()
        .loginId("empty")
        .id(2L)
        .build();
    Comment comment = Comment.builder()
        .id(1L)
        .member(member)
        .content("테스트 댓글입니다.")
        .build();

    when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));

    // when & then

    // deleteComment() 메서드의 작성자가 아닌 다른 사용자의 정보를 넣음
    assertThrows(UnauthorizedMemberException.class,
        () -> commentService.deleteComment(CustomMemberDetails.fromMember(member2), commentId));
  }

  @Test
  @DisplayName("댓글 조회하는 기능 테스트")
  //given
  void getCommentList(){
    when(reviewRepository.findById(1L)).thenReturn(
        Optional.of(
            Review.builder()
            .id(1L)
            .build())
    );


    List<Comment> commentList = new ArrayList<>();
    commentList.add(
        Comment.builder()
            .id(1L)
            .member(member)
            .review(review)
            .content("테스트 댓글1")
            .build()
    );
    commentList.add(
        Comment.builder()
            .id(2L)
            .member(member)
            .review(review)
            .content("테스트 댓글1")
            .build()
    );
    PageRequest pageRequest = PageRequest.of(0, 10);
    Page<Comment> page = new PageImpl<>(commentList,pageRequest,commentList.size());

    when(commentRepository.findCommentsByReviewSortDate(any(Pageable.class),any(Review.class)))
        .thenReturn(page);

    //when
    CommentResponse result = commentService
        .getCommentList(1L, 1, null);

    //then
    assertEquals("수빈1",result.getComments().get(1).getMemberNickname());

  }

  @Test
  @DisplayName("댓글 좋아요 기능 테스트 - 성공")
  void likeComment_success() {
    // given
    when(commentRepository.findById(comment.getId())).thenReturn(Optional.of(comment));
    when(commentLikeRepository.existsByCommentIdAndMemberId(comment.getId(), member.getId())).thenReturn(false);

    ArgumentCaptor<CommentLike> commentLikeCaptor = ArgumentCaptor.forClass(CommentLike.class);
    ArgumentCaptor<Long> commentIdCaptor = ArgumentCaptor.forClass(Long.class);

    // when
    assertDoesNotThrow(() -> commentService.likeComment(memberDetails, comment.getId()));

    // then
    verify(commentLikeRepository, times(1)).save(commentLikeCaptor.capture());
    verify(commentRepository, times(1)).increaseLikeCount(commentIdCaptor.capture());

    assertEquals(comment.getId(), commentIdCaptor.getValue());
    assertEquals(member.getId(), commentLikeCaptor.getValue().getMember().getId());
    assertEquals(comment.getId(), commentLikeCaptor.getValue().getComment().getId());
  }

  @Test
  @DisplayName("댓글 좋아요 취소 기능 테스트 - 성공")
  void unlikeComment_success() {
    // given
    CommentLike commentLike = new CommentLike(comment, member);

    when(commentLikeRepository.findByCommentIdAndMemberId(comment.getId(), member.getId()))
            .thenReturn(Optional.of(commentLike));

    ArgumentCaptor<CommentLike> commentLikeCaptor = ArgumentCaptor.forClass(CommentLike.class);
    ArgumentCaptor<Long> commentIdCaptor = ArgumentCaptor.forClass(Long.class);

    // when
    assertDoesNotThrow(() -> commentService.unlikeComment(memberDetails, comment.getId()));

    // then
    verify(commentLikeRepository, times(1)).delete(commentLikeCaptor.capture());
    verify(commentRepository, times(1)).decreaseLikeCount(commentIdCaptor.capture());

    assertEquals(comment.getId(), commentLikeCaptor.getValue().getComment().getId());
    assertEquals(member.getId(), commentLikeCaptor.getValue().getMember().getId());
  }

  @Test
  @DisplayName("댓글 좋아요 기능 테스트 - 실패 (이미 좋아요한 경우)")
  void likeComment_fail_alreadyLiked() {
    // given
    when(commentRepository.findById(comment.getId())).thenReturn(Optional.of(comment));
    when(commentLikeRepository.existsByCommentIdAndMemberId(comment.getId(), member.getId()))
            .thenReturn(true);

    // when & then
    assertThrows(AlreadyLikedException.class, () -> commentService.likeComment(memberDetails, comment.getId()));

    // 좋아요가 추가되지 않아야 함
    verify(commentLikeRepository, never()).save(any(CommentLike.class));
    verify(commentRepository, never()).increaseLikeCount(anyLong());
  }

  @Test
  @DisplayName("댓글 좋아요 취소 기능 테스트 - 실패 (좋아요한 적 없는 경우)")
  void unlikeComment_fail_notLiked() {
    // given
    when(commentLikeRepository.findByCommentIdAndMemberId(comment.getId(), member.getId()))
            .thenReturn(Optional.empty());

    // when & then
    assertThrows(LikeNotFoundException.class, () -> commentService.unlikeComment(memberDetails, comment.getId()));

    // 좋아요 삭제나 감소가 일어나지 않아야 함
    verify(commentLikeRepository, never()).delete(any(CommentLike.class));
    verify(commentRepository, never()).decreaseLikeCount(anyLong());
  }
}