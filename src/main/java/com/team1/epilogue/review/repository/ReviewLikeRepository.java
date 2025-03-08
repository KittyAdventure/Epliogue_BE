package com.team1.epilogue.review.repository;

import com.team1.epilogue.review.entity.ReviewLike;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReviewLikeRepository extends JpaRepository<ReviewLike, Long> {
    Optional<ReviewLike> findByReviewIdAndMemberId(Long reviewId, Long memberId);

    Boolean existsByReviewIdAndMemberId(Long reviewId, Long memberId);

}
