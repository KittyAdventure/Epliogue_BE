package com.team1.epilogue.follow.service;

import com.team1.epilogue.follow.dto.FollowActionResponse;
import com.team1.epilogue.follow.dto.MemberDto;
import com.team1.epilogue.follow.dto.PaginationDto;
import com.team1.epilogue.follow.dto.ReviewDto;
import com.team1.epilogue.follow.dto.ReviewListResponse;
import com.team1.epilogue.follow.entity.Follow;
import com.team1.epilogue.auth.entity.Member;
import com.team1.epilogue.review.entity.Review;
import com.team1.epilogue.follow.repository.FollowRepository;
import com.team1.epilogue.auth.repository.MemberRepository;
import com.team1.epilogue.review.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FollowService {

    private final MemberRepository memberRepository;
    private final FollowRepository followRepository;
    private final ReviewRepository reviewRepository;

    /**
     * 회원이 다른 회원을 팔로우
     *
     * @param followerLoginId 팔로우 요청을 보낸 회원의 로그인 ID
     * @param targetLoginId   팔로우 대상 회원의 로그인 ID
     * @return FollowActionResponse 팔로우 성공 메시지와 관련 정보
     */
    public FollowActionResponse followUser(String followerLoginId, String targetLoginId) {
        Member follower = memberRepository.findByLoginId(followerLoginId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid authentication"));
        Member followed = memberRepository.findByLoginId(targetLoginId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Target user not found"));

        followRepository.findByFollowerAndFollowed(follower, followed).ifPresent(f -> {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Already following");
        });

        Follow follow = Follow.builder()
                .follower(follower)
                .followed(followed)
                .build();
        followRepository.save(follow);
        return new FollowActionResponse("Follow creation successful", follower.getLoginId(), followed.getLoginId());
    }

    /**
     * 회원이 특정 회원을 언팔로우
     *
     * @param followerLoginId 언팔로우 요청을 보낸 회원의 로그인 ID
     * @param targetLoginId   언팔로우 대상 회원의 로그인 ID
     */
    public void unfollowUser(String followerLoginId, String targetLoginId) {
        Member follower = memberRepository.findByLoginId(followerLoginId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid authentication"));
        Member followed = memberRepository.findByLoginId(targetLoginId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Target user not found"));

        Follow follow = followRepository.findByFollowerAndFollowed(follower, followed)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Follow relationship does not exist"));
        followRepository.delete(follow);
    }

    /**
     * 특정 회원이 팔로우한 회원 목록 조회 (Fetch Join 사용)
     *
     * @param loginId 조회할 회원의 로그인 ID
     * @return List<MemberDto> 팔로우한 회원들의 목록
     */
    public List<MemberDto> getFollowingList(String loginId) {
        Member member = memberRepository.findByLoginId(loginId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid authentication"));

        List<Follow> followings = followRepository.findByFollowerWithFollowed(member);
        return followings.stream()
                .map(f -> convertToMemberDto(f.getFollowed()))
                .collect(Collectors.toList());
    }

    /**
     * 특정 회원을 팔로우하는 회원 목록 조회 (Fetch Join 사용)
     *
     * @param loginId 조회할 회원의 로그인 ID
     * @return List<MemberDto> 팔로우하는 회원들의 목록
     */
    public List<MemberDto> getFollowersList(String loginId) {
        Member member = memberRepository.findByLoginId(loginId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid authentication"));

        List<Follow> followers = followRepository.findByFollowedWithFollower(member);
        return followers.stream()
                .map(f -> convertToMemberDto(f.getFollower()))
                .collect(Collectors.toList());
    }

    /**
     * 특정 회원이 팔로우한 회원들의 리뷰 목록 조회
     *
     * @param currentLoginId 조회를 요청한 회원의 로그인 ID
     * @param page           요청 페이지 번호 (1 이상)
     * @param limit          페이지당 리뷰 개수 (1 이상)
     * @param sort           정렬 방식 ("asc" 또는 "desc")
     * @return ReviewListResponse 팔로우한 회원들의 리뷰 목록과 페이지네이션 정보
     */
    public ReviewListResponse getFollowedReviews(String currentLoginId, int page, int limit, String sort) {
        if (page < 1 || limit < 1) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid query parameters");
        }

        Member currentMember = memberRepository.findByLoginId(currentLoginId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid authentication"));

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

        Page<Review> reviewPage = reviewRepository.findByMemberInWithFetchJoin(followedMembers, pageable);
        List<ReviewDto> reviewDtos = reviewPage.stream()
                .map(this::convertToReviewDto)
                .collect(Collectors.toList());

        PaginationDto pagination = new PaginationDto(page, limit, reviewPage.getTotalElements());
        return new ReviewListResponse(reviewDtos, pagination);
    }


    /**
     * Member 엔터티를 MemberDto로 변환
     *
     * @param member 변환할 Member 엔터티
     * @return MemberDto 변환된 회원 정보 DTO
     */
    private MemberDto convertToMemberDto(Member member) {
        return new MemberDto(String.valueOf(member.getId()), member.getLoginId(), member.getNickname(), member.getProfileUrl());
    }

    /**
     * Review 엔터티를 ReviewDto로 변환 (빌더 패턴 사용 및 @JsonFormat 활용)
     *
     * @param review 변환할 Review 엔터티
     * @return ReviewDto 변환된 리뷰 정보 DTO
     */
    private ReviewDto convertToReviewDto(Review review) {
        return ReviewDto.builder()
                .id(String.valueOf(review.getId()))
                .bookId(review.getBook() != null ? String.valueOf(review.getBook().getId()) : null)
                .content(review.getContent())
                .imageUrl(review.getImageUrl())
                .createdAt(review.getCreatedAt())
                .member(convertToMemberDto(review.getMember()))
                .build();
    }
}
