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

/**
 * [클래스 레벨]
 * FollowService의 구현체로, 팔로우 관련 비즈니스 로직을 처리하는 서비스 클래스
 *
 * - 회원 팔로우 및 언팔로우
 * - 팔로잉 및 팔로워 목록 조회
 * - 팔로우한 회원들의 리뷰 조회
 */
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

    /**
     * [메서드 레벨]
     * 회원이 다른 회원을 팔로우
     *
     * @param followerLoginId 팔로우 요청을 보낸 회원의 로그인 ID
     * @param targetLoginId 팔로우 대상 회원의 로그인 ID
     * @return FollowActionResponse 팔로우 성공 메시지와 관련 정보
     */
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

    /**
     * [메서드 레벨]
     * 회원이 특정 회원을 언팔로우
     *
     * @param followerLoginId 언팔로우 요청을 보낸 회원의 로그인 ID
     * @param targetLoginId 언팔로우 대상 회원의 로그인 ID
     */
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

    /**
     * [메서드 레벨]
     * 특정 회원이 팔로우한 회원 목록을 조회
     *
     * @param loginId 조회할 회원의 로그인 ID
     * @return List<MemberDto> 팔로우한 회원들의 목록
     */
    @Override
    public List<MemberDto> getFollowingList(String loginId) {
        Member member = memberRepository.findByLoginId(loginId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "잘못된 인증"));

        List<Follow> followings = followRepository.findByFollower(member);
        return followings.stream()
                .map(f -> convertToMemberDto(f.getFollowed()))
                .collect(Collectors.toList());
    }

    /**
     * [메서드 레벨]
     * 특정 회원을 팔로우하는 회원 목록을 조회
     *
     * @param loginId 조회할 회원의 로그인 ID
     * @return List<MemberDto> 팔로우하는 회원들의 목록
     */
    @Override
    public List<MemberDto> getFollowersList(String loginId) {
        Member member = memberRepository.findByLoginId(loginId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "잘못된 인증"));

        List<Follow> followers = followRepository.findByFollowed(member);
        return followers.stream()
                .map(f -> convertToMemberDto(f.getFollower()))
                .collect(Collectors.toList());
    }

    /**
     * [메서드 레벨]
     * 특정 회원이 팔로우한 회원들의 리뷰 목록을 조회
     *
     * @param currentLoginId 조회를 요청한 회원의 로그인 ID
     * @param page 요청 페이지 번호 (1 이상)
     * @param limit 페이지당 리뷰 개수 (1 이상)
     * @param sort 정렬 방식 ("asc" 또는 "desc")
     * @return ReviewListResponse 팔로우한 회원들의 리뷰 목록과 페이지네이션 정보
     */
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

        // 정렬 방식 결정 (기본적으로 최신순)
        Sort sortObj = sort.equalsIgnoreCase("asc") ? Sort.by("createdAt").ascending() : Sort.by("createdAt").descending();
        Pageable pageable = PageRequest.of(page - 1, limit, sortObj);

        // 팔로우한 회원들의 리뷰 조회
        Page<Review> reviewPage = reviewRepository.findByMemberIn(followedMembers, pageable);
        List<ReviewDto> reviewDtos = reviewPage.stream()
                .map(this::convertToReviewDto)
                .collect(Collectors.toList());

        PaginationDto pagination = new PaginationDto(page, limit, reviewPage.getTotalElements());
        return new ReviewListResponse(reviewDtos, pagination);
    }

    /**
     * [메서드 레벨]
     * Member 엔터티를 MemberDto로 변환
     *
     * @param member 변환할 Member 엔터티
     * @return MemberDto 변환된 회원 정보 DTO
     */
    private MemberDto convertToMemberDto(Member member) {
        return new MemberDto(String.valueOf(member.getId()), member.getLoginId(), member.getNickname(), member.getProfileUrl());
    }

    /**
     * [메서드 레벨]
     * Review 엔터티를 ReviewDto로 변환
     *
     * @param review 변환할 Review 엔터티
     * @return ReviewDto 변환된 리뷰 정보 DTO
     */
    private ReviewDto convertToReviewDto(Review review) {
        return new ReviewDto(
                String.valueOf(review.getId()),
                review.getBook() != null ? String.valueOf(review.getBook().getId()) : null,
                review.getContent(),
                review.getImageUrl(),
                review.getCreatedAt() != null ? review.getCreatedAt().toString() : null,
                convertToMemberDto(review.getMember())
        );
    }
}
