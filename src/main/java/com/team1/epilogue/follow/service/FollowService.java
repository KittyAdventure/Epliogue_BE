package com.team1.epilogue.follow.service;

import com.team1.epilogue.follow.dto.FollowActionResponse;
import com.team1.epilogue.follow.dto.MemberDto;
import com.team1.epilogue.follow.dto.ReviewListResponse;

import java.util.List;

/**
 * [클래스 레벨]
 * FollowService는 회원 간 팔로우 관련 기능을 제공하는 서비스 인터페이스
 *
 * 주요 기능:
 * - 회원 팔로우
 * - 회원 언팔로우
 * - 특정 회원이 팔로우한 회원 목록 조회
 * - 특정 회원을 팔로우하는 회원 목록 조회
 * - 특정 회원이 팔로우한 회원들의 리뷰 조회
 */
public interface FollowService {
    FollowActionResponse followUser(String followerLoginId, String targetLoginId);
    void unfollowUser(String followerLoginId, String targetLoginId);
    List<MemberDto> getFollowingList(String loginId);
    List<MemberDto> getFollowersList(String loginId);
    ReviewListResponse getFollowedReviews(String currentLoginId, int page, int limit, String sort);
}