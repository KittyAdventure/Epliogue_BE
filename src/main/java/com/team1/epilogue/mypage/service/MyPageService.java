package com.team1.epilogue.mypage.service;

import com.team1.epilogue.auth.entity.Member;
import com.team1.epilogue.auth.exception.MemberNotFoundException;
import com.team1.epilogue.auth.repository.MemberRepository;
import com.team1.epilogue.auth.security.CustomMemberDetails;
import com.team1.epilogue.comment.entity.Comment;
import com.team1.epilogue.comment.repository.CommentRepository;
import com.team1.epilogue.mypage.dto.MyPageCommentsDetailResponse;
import com.team1.epilogue.mypage.dto.MyPageCommentsResponse;
import com.team1.epilogue.mypage.dto.MyPageUserInfoResponse;
import com.team1.epilogue.review.repository.ReviewRepository;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import com.team1.epilogue.follow.repository.FollowRepository;

@Service
@RequiredArgsConstructor
public class MyPageService {

  private final CommentRepository commentRepository;
  private final MemberRepository memberRepository;
  private final FollowRepository followRepository;
  private final ReviewRepository reviewRepository;

  public MyPageCommentsResponse getMyComments(CustomMemberDetails details, int page) {
    PageRequest pageRequest = PageRequest.of(page - 1, 20);

    Member member = details.getMember();

    Page<Comment> result = commentRepository.findAllByMemberId(pageRequest, member);

    List<MyPageCommentsDetailResponse> list = new ArrayList<>();

    // 가져온 Page 객체에서 프론트로 줄 데이터 가공
    result.getContent().stream().forEach( data ->
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

  public MyPageUserInfoResponse getUserInfo(String memberId) {
    Member member = memberRepository.findByLoginId(memberId).orElseThrow(
        () -> new MemberNotFoundException("존재하지 않는 회원입니다.")
    );

    return MyPageUserInfoResponse.builder()
        .nickName(member.getNickname())
        .loginId(member.getLoginId())
        .email(member.getEmail())
        .follower(followRepository.findByFollower(member).size())
        .following(followRepository.findByFollowed(member).size())
        .reviewCount(reviewRepository.findAllByMember(member).size())
//        .meetingCount(meetingRepository.findAll)
//        .collectionCount(collecccf)
        .commentCount(commentRepository.findAllByMember(member).size())
        .point(member.getPoint())
        .build();
  }
}
