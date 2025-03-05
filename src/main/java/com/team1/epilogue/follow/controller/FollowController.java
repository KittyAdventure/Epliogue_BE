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

@RestController
@RequestMapping("/api/follows")
public class FollowController {

    private final FollowService followService;

    public FollowController(FollowService followService) {
        this.followService = followService;
    }

    // 팔로우 등록
    @PostMapping("/{targetLoginId}")
    public FollowActionResponse followUser(@PathVariable("targetLoginId") String targetLoginId) {
        String currentLoginId = getCurrentLoginId();
        return followService.followUser(currentLoginId, targetLoginId);
    }

    // 팔로우 삭제
    @DeleteMapping("/{targetLoginId}")
    public MessageResponse unfollowUser(@PathVariable("targetLoginId") String targetLoginId) {
        String currentLoginId = getCurrentLoginId();
        followService.unfollowUser(currentLoginId, targetLoginId);
        return new MessageResponse("팔로우 삭제 성공");
    }

    // 팔로워/팔로잉 목록 조회
    @GetMapping("/{loginId}")
    public MembersResponse getFollowList(@PathVariable("loginId") String loginId,
                                         @RequestParam("type") String type) {
        if ("following".equalsIgnoreCase(type)) {
            return new MembersResponse(followService.getFollowingList(loginId));
        } else if ("followers".equalsIgnoreCase(type)) {
            return new MembersResponse(followService.getFollowersList(loginId));
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "잘못된 쿼리 파라미터");
        }
    }

    // 팔로우한 회원의 리뷰 조회
    @GetMapping("/review")
    public ReviewListResponse getFollowedReviews(@RequestParam("page") int page,
                                                 @RequestParam("limit") int limit,
                                                 @RequestParam("sort") String sort) {
        String currentLoginId = getCurrentLoginId();
        return followService.getFollowedReviews(currentLoginId, page, limit, sort);
    }

    private String getCurrentLoginId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "인증되지 않은 사용자입니다.");
        }
        Object principal = authentication.getPrincipal();
        if (principal instanceof CustomMemberDetails) {
            return ((CustomMemberDetails) principal).getUsername();
        }
        if (principal instanceof String) {
            return (String) principal;
        }
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "인증 정보를 확인할 수 없습니다.");
    }
}
