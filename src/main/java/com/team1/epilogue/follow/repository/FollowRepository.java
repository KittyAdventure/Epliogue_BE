package com.team1.epilogue.follow.repository;

import com.team1.epilogue.follow.entity.Follow;
import com.team1.epilogue.auth.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FollowRepository extends JpaRepository<Follow, Long> {

    Optional<Follow> findByFollowerAndFollowed(Member follower, Member followed);

    // 팔로잉 목록
    List<Follow> findByFollower(Member follower);

    // 팔로워 목록
    List<Follow> findByFollowed(Member followed);

    void deleteByFollowerAndFollowed(Member follower, Member followed);
}
