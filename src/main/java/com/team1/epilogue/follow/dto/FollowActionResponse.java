package com.team1.epilogue.follow.dto;

import lombok.AllArgsConstructor;
import lombok.Data;


/**
 * [클래스 레벨]
 * FollowActionResponse 클래스는 팔로우 또는 언팔로우 요청의 응답 데이터를 담는 DTO
 *
 * 필드:
 * - message: 요청 처리 결과 메시지 (예: "팔로우 생성 성공", "팔로우 삭제 성공")
 * - followerLoginId: 팔로우를 수행한 사용자의 로그인 ID
 * - followedLoginId: 팔로우된 사용자의 로그인 ID
 */
@Data
@AllArgsConstructor
public class FollowActionResponse {
    private String message;
    private String followerLoginId;
    private String followedLoginId;
}
