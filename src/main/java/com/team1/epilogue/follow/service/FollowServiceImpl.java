package com.team1.epilogue.follow.service;

import com.team1.epilogue.follow.dto.*;
import com.team1.epilogue.follow.entity.Follow;
import com.team1.epilogue.auth.entity.Member;
import com.team1.epilogue.review.entity.Review;
import com.team1.epilogue.follow.repository.FollowRepository;
import com.team1.epilogue.auth.repository.MemberRepository;
import com.team1.epilogue.review.repository.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;



@Service
public class FollowServiceImpl implements FollowService {

    private final MemberRepository memberRepository;
    private final FollowRepository followRepository;
    private final ReviewRepository reviewRepository;

    @Autowired
    public FollowServiceImpl(MemberRepository memberRepository,
                             FollowRepository followRepository,
                             ReviewRepository reviewRepository) {
        this.memberRepository = memberRepository;
        this.followRepository = followRepository;
        this.reviewRepository = reviewRepository;
    }

    @Override
    public FollowActionResponse followUser(String followerLoginId, String targetLoginId) {
        Member follower = memberRepository.findByLoginId(followerLoginId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "잘못된 인증"));
        Member followed = memberRepository.findByLoginId(targetLoginId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "대상 사용자가 존재하지 않음"));

        followRepository.findByFollowerAndFollowed(follower, followed).ifPresent(f -> {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "이미 팔로우 상태입니다.");
        });

        Follow follow = Follow.builder()
                .follower(follower)
                .followed(followed)
                .build();
        followRepository.save(follow);
        return new FollowActionResponse("팔로우 생성 성공", follower.getLoginId(), followed.getLoginId());
    }

    @Override
    public void unfollowUser(String followerLoginId, String targetLoginId) {
        Member follower = memberRepository.findByLoginId(followerLoginId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "잘못된 인증"));
        Member followed = memberRepository.findByLoginId(targetLoginId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "대상 사용자가 존재하지 않음"));

        Follow follow = followRepository.findByFollowerAndFollowed(follower, followed)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "팔로우 관계가 존재하지 않습니다."));
        followRepository.delete(follow);
    }

    @Override
    public List<MemberDto> getFollowingList(String loginId) {
        Member member = memberRepository.findByLoginId(loginId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "잘못된 인증"));
        List<Follow> followings = followRepository.findByFollower(member);
        return followings.stream()
                .map(f -> convertToMemberDto(f.getFollowed()))
                .collect(Collectors.toList());
    }

    @Override
    public List<MemberDto> getFollowersList(String loginId) {
        Member member = memberRepository.findByLoginId(loginId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "잘못된 인증"));
        List<Follow> followers = followRepository.findByFollowed(member);
        return followers.stream()
                .map(f -> convertToMemberDto(f.getFollower()))
                .collect(Collectors.toList());
    }

    @Override
    public ReviewListResponse getFollowedReviews(String currentLoginId, int page, int limit, String sort) {
        if (page < 1 || limit < 1) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "잘못된 쿼리 파라미터");
        }
        Member currentMember = memberRepository.findByLoginId(currentLoginId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "잘못된 인증"));
        List<Follow> followings = followRepository.findByFollower(currentMember);
        List<Member> followedMembers = followings.stream()
                .map(Follow::getFollowed)
                .collect(Collectors.toList());
        if (followedMembers.isEmpty()) {
            PaginationDto pagination = new PaginationDto(page, limit, 0);
            return new ReviewListResponse(List.of(), pagination);
        }
        Sort sortObj = sort.equalsIgnoreCase("asc") ? Sort.by("createdAt").ascending() : Sort.by("createdAt").descending();
        Pageable pageable = PageRequest.of(page - 1, limit, sortObj);
        Page<Review> reviewPage = reviewRepository.findByMemberIn(followedMembers, pageable);
        List<ReviewDto> reviewDtos = reviewPage.stream()
                .map(this::convertToReviewDto)
                .collect(Collectors.toList());
        PaginationDto pagination = new PaginationDto(page, limit, reviewPage.getTotalElements());
        return new ReviewListResponse(reviewDtos, pagination);
    }

    private MemberDto convertToMemberDto(Member member) {
        return new MemberDto(String.valueOf(member.getId()), member.getLoginId(), member.getNickname(), member.getProfileUrl());
    }

    private ReviewDto convertToReviewDto(Review review) {
        return new ReviewDto(
                String.valueOf(review.getId()),
                String.valueOf(review.getBook().getId()),
                review.getContent(),
                review.getImageUrl(),
                review.getCreatedAt() != null ? review.getCreatedAt().toString() : null,
                convertToMemberDto(review.getMember())
        );
    }
}
