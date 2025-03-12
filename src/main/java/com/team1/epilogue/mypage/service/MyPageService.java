package com.team1.epilogue.mypage.service;

import com.team1.epilogue.auth.entity.Member;
import com.team1.epilogue.auth.exception.MemberNotFoundException;
import com.team1.epilogue.auth.repository.MemberRepository;
import com.team1.epilogue.comment.entity.Comment;
import com.team1.epilogue.comment.repository.CommentRepository;
import com.team1.epilogue.mypage.dto.MyPageCommentsDetailResponse;
import com.team1.epilogue.mypage.dto.MyPageCommentsResponse;
import com.team1.epilogue.mypage.dto.MyPageReviewDetail;
import com.team1.epilogue.mypage.dto.MyPageReviewsResponse;
import com.team1.epilogue.review.entity.Review;
import com.team1.epilogue.review.repository.ReviewRepository;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MyPageService {

  private final CommentRepository commentRepository;
  private final ReviewRepository reviewRepository;
  private final MemberRepository memberRepository;

  public MyPageCommentsResponse getMyComments(Member member, int page) {
    PageRequest pageRequest = PageRequest.of(page - 1, 20);

    Page<Comment> result = commentRepository.findAllByMemberId(pageRequest, member);

    List<MyPageCommentsDetailResponse> list = new ArrayList<>();

    // 가져온 Page 객체에서 프론트로 줄 데이터 가공
    result.getContent().stream().forEach(data ->
        list.add(MyPageCommentsDetailResponse.builder() // 댓글 Detail 을 list 에 담아준다.
            .postDateTime(data.getCreatedAt()) // 생성일
            .bookTitle(data.getReview().getBook().getTitle()) // 책제목
            .content(data.getContent()) // 댓글 내용
            .build()) // build 해서 List<MyPageCommentsDetailResponse> 에 add
    );

    return MyPageCommentsResponse.builder()
        .totalPage(result.getTotalPages()) // 총 페이지 번호
        .page(page) // 현재 페이지 번호
        .comments(list) // JSON 내부 배열로 반환
        .build();
  }

  public MyPageReviewsResponse getReviewsByMember(String memberId, int page) {
    Member member = memberRepository.findByLoginId(memberId).orElseThrow(
        () -> new MemberNotFoundException("사용자 정보가 존재하지 않습니다.")
    );
    Page<Review> reviews = reviewRepository.findByMemberId(memberId, PageRequest.of(page - 1, 6));

    List<MyPageReviewDetail> list = new ArrayList<>();

    reviews.getContent().stream().forEach(
        data -> {
          list.add( // 각각의 Review Entity 들을 DTO 로 변환
              MyPageReviewDetail.builder()
                  .reviewId(data.getId())
                  .reviewBookTitle(data.getBook().getTitle())
                  .reviewBookAuthor(data.getBook().getAuthor())
                  .reviewContent(data.getContent())
                  .reviewBookPubYear(data.getBook().getPubDate().getYear())
                  .reviewCommentsCount(data.getCommentsCount())
                  .thumbnail(data.getBook().getCoverUrl())
                  .build()
          );
        }
    );

    return MyPageReviewsResponse.builder()
        .userNickname(member.getNickname())
        .reviews(list)
        .totalPages(reviews.getTotalPages())
        .build();
  }
}
