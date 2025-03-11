package com.team1.epilogue.mypage.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.team1.epilogue.auth.entity.Member;
import com.team1.epilogue.auth.security.CustomMemberDetails;
import com.team1.epilogue.book.entity.Book;
import com.team1.epilogue.comment.entity.Comment;
import com.team1.epilogue.comment.repository.CommentRepository;
import com.team1.epilogue.mypage.dto.MyPageCommentsResponse;
import com.team1.epilogue.review.entity.Review;
import java.util.ArrayList;
import java.util.List;
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

@ExtendWith(MockitoExtension.class)
class MyPageServiceTest {

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
              .id((long)i)
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
    MyPageCommentsResponse result = myPageService.getMyComments(
        CustomMemberDetails.fromMember(testMember), 1);

    //then
    assertEquals(10, result.getComments().size());
    assertEquals(1, result.getTotalPage());
    assertEquals("10번째 테스트댓글",result.getComments().get(9).getContent());
  }
}