package com.team1.epilogue.review.repository;

import com.team1.epilogue.review.entity.ReviewLike;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ReviewLikeRepository extends JpaRepository<ReviewLike, Long> {

  Optional<ReviewLike> findByReviewIdAndMemberId(Long reviewId, Long memberId);

  Boolean existsByReviewIdAndMemberId(Long reviewId, Long memberId);

  // 로그인한 사용자가 좋아요한 리뷰 ID들을 한 번의 쿼리로 조회 (N+1 문제 방지)
  @Query("SELECT rl.review.id FROM ReviewLike rl WHERE rl.member.id = :memberId AND rl.review.id IN :reviewIds")
  List<Long> findLikedReviewIdsByMemberId(Long memberId, List<Long> reviewIds);
}
