package com.team1.epilogue.mypage.service;

import com.team1.epilogue.auth.entity.Member;
import com.team1.epilogue.comment.entity.Comment;
import com.team1.epilogue.comment.repository.CommentRepository;
import com.team1.epilogue.mypage.dto.MyPageCalendarDetail;
import com.team1.epilogue.mypage.dto.MyPageCalendarResponse;
import com.team1.epilogue.mypage.dto.MyPageCommentsDetailResponse;
import com.team1.epilogue.mypage.dto.MyPageCommentsResponse;
import com.team1.epilogue.review.entity.Review;
import com.team1.epilogue.review.repository.ReviewRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MyPageService {

  private final CommentRepository commentRepository;
  private final ReviewRepository reviewRepository;

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

  public List<MyPageCalendarResponse> getCalendar(String memberId, String date) {
    LocalDate dateData = LocalDate.parse(date);

    // 해당 날짜의 시작 시간 (00:00:00)
    LocalDateTime startDateTime = dateData.atStartOfDay();

    // 해당 날짜의 마지막 날 (23:59:59)
    LocalDateTime endDateTime = dateData.withDayOfMonth(dateData.lengthOfMonth())
        .atTime(23, 59, 59);

    // 리뷰 데이터 조회
    List<Review> reviews = reviewRepository.findByDateAndMember(startDateTime, endDateTime,
        memberId);

    // 리뷰 데이터를 날짜별로 그룹화
    Map<String, List<MyPageCalendarDetail>> groupedReviews = reviews.stream()
        .map(review -> {
          MyPageCalendarDetail dto = MyPageCalendarDetail.builder()
              .thumbnail((review.getBook().getCoverUrl()))
              .bookTitle((review.getBook().getTitle()))
              .reviewPostDateTime(review.getCreatedAt())
              .build();
          return dto;
        })
        .collect(Collectors.groupingBy(
            review -> review.getReviewPostDateTime().toLocalDate().toString()  // 날짜별로 그룹화
        ));

    // 각 날짜별로 count와 data를 포함한 ReviewDateDto 리스트로 변환
    List<MyPageCalendarResponse> result = groupedReviews.entrySet().stream()
        .map(entry -> {
          MyPageCalendarResponse dto = MyPageCalendarResponse.builder()
              .date((entry.getKey()))
              .count(entry.getValue().size())
              .data((entry.getValue()))
              .build();
          return dto;
        })
        .collect(Collectors.toList());

    return result;
  }
}
