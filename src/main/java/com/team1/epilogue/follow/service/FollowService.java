package com.team1.epilogue.follow.service;

import com.team1.epilogue.follow.dto.FollowActionResponse;
import com.team1.epilogue.follow.dto.MessageResponse;
import com.team1.epilogue.follow.dto.MemberDto;
import com.team1.epilogue.follow.dto.ReviewListResponse;

import java.util.List;

public interface FollowService {
    FollowActionResponse followUser(String followerLoginId, String targetLoginId);
    void unfollowUser(String followerLoginId, String targetLoginId);
    List<MemberDto> getFollowingList(String loginId);
    List<MemberDto> getFollowersList(String loginId);
    ReviewListResponse getFollowedReviews(String currentLoginId, int page, int limit, String sort);
}
