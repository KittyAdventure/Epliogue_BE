package com.team1.epilogue.follow.repository;

import com.team1.epilogue.follow.entity.Follow;
import com.team1.epilogue.auth.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * [클래스 레벨]
 * FollowRepository는 회원 간의 팔로우 관계를 관리하는 JPA 리포지토리이
 *
 * 주요 기능:
 * - 특정 회원이 특정 회원을 팔로우했는지 확인
 * - 특정 회원이 팔로우한 회원 목록 조회
 * - 특정 회원을 팔로우하는 회원 목록 조회
 * - 팔로우 관계 삭제
 */
public interface FollowRepository extends JpaRepository<Follow, Long> {

    Optional<Follow> findByFollowerAndFollowed(Member follower, Member followed);
    List<Follow> findByFollower(Member follower);
    List<Follow> findByFollowed(Member followed);
    void deleteByFollowerAndFollowed(Member follower, Member followed);
}