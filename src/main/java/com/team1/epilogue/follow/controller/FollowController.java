package com.team1.epilogue.follow.controller;

import com.team1.epilogue.auth.security.CustomMemberDetails;
import com.team1.epilogue.follow.dto.FollowActionResponse;
import com.team1.epilogue.follow.dto.MessageResponse;
import com.team1.epilogue.follow.dto.MembersResponse;
import com.team1.epilogue.follow.dto.ReviewListResponse;
import com.team1.epilogue.follow.service.FollowService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

/**
 * [클래스 레벨]
 * 팔로우 관련 기능을 제공하는 컨트롤러
 * 사용자를 팔로우 및 언팔로우
 * 특정 사용자의 팔로워 및 팔로잉 목록 조회
 * 팔로우한 사용자의 리뷰 조회
 *
 * 인증된 사용자만 API를 사용할 수 있으며, 로그인된 사용자의 정보를 SecurityContextHolder에서 가져옴
 */
@RestController
@RequestMapping("/api/follows")
public class FollowController {

    private final FollowService followService;

    public FollowController(FollowService followService) {
        this.followService = followService;
    }

    /**
     * [메서드 레벨]
     * 특정 사용자를 팔로우
     *
     * @param targetLoginId 팔로우할 사용자의 로그인 ID
     * @return 팔로우 성공 응답 (팔로워, 팔로우된 사용자 정보 포함)
     */
    @PostMapping("/{targetLoginId}")
    public FollowActionResponse followUser(@PathVariable("targetLoginId") String targetLoginId) {
        String currentLoginId = getCurrentLoginId();
        return followService.followUser(currentLoginId, targetLoginId);
    }

    /**
     * [메서드 레벨]
     * 특정 사용자의 팔로우를 해제
     *
     * @param targetLoginId 언팔로우할 사용자의 로그인 ID
     * @return 언팔로우 성공 메시지 응답
     */
    @DeleteMapping("/{targetLoginId}")
    public MessageResponse unfollowUser(@PathVariable("targetLoginId") String targetLoginId) {
        String currentLoginId = getCurrentLoginId();
        followService.unfollowUser(currentLoginId, targetLoginId);
        return new MessageResponse("Unfollow action successful");
    }

    /**
     * [메서드 레벨]
     * 특정 사용자의 팔로워 또는 팔로잉 목록 조회
     *
     * @param loginId 조회할 사용자의 로그인 ID
     * @param type 조회할 목록 유형 (following 또는 followers)
     * @return 해당 목록의 사용자 정보 리스트
     * @throws ResponseStatusException 잘못된 쿼리 파라미터 입력 시 400 에러 반환
     */
    @GetMapping("/{loginId}")
    public MembersResponse getFollowList(@PathVariable("loginId") String loginId,
                                         @RequestParam("type") String type) {
        if ("following".equalsIgnoreCase(type)) {
            return new MembersResponse(followService.getFollowingList(loginId));
        } else if ("followers".equalsIgnoreCase(type)) {
            return new MembersResponse(followService.getFollowersList(loginId));
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid query parameter");
        }
    }

    /**
     * [메서드 레벨]
     * 로그인한 사용자가 팔로우한 사용자의 리뷰를 조회
     *
     * @param page 페이지 번호
     * @param limit 페이지당 아이템 수
     * @param sort 정렬 순서 (asc 또는 desc)
     * @return 팔로우한 사용자의 리뷰 리스트
     */
    @GetMapping("/review")
    public ReviewListResponse getFollowedReviews(@RequestParam("page") int page,
                                                 @RequestParam("limit") int limit,
                                                 @RequestParam("sort") String sort) {
        String currentLoginId = getCurrentLoginId();
        return followService.getFollowedReviews(currentLoginId, page, limit, sort);
    }

    /**
     * [메서드 레벨]
     * 현재 로그인한 사용자의 ID를 가져옴
     *
     * @return 현재 로그인한 사용자의 ID
     * @throws ResponseStatusException 인증되지 않은 경우 401 에러 발생
     */
    private String getCurrentLoginId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User is not authenticated.");
        }
        Object principal = authentication.getPrincipal();
        System.out.println("Principal 클래스: " + principal.getClass().getName());
        if (principal instanceof CustomMemberDetails) {
            return ((CustomMemberDetails) principal).getUsername();
        }
        if (principal instanceof org.springframework.security.core.userdetails.User) {
            return ((org.springframework.security.core.userdetails.User) principal).getUsername();
        }
        if (principal instanceof String) {
            return (String) principal;
        }
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unable to determine authentication details.");
    }
}