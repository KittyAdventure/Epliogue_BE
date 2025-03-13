package com.team1.epilogue.comment.repository;

import com.team1.epilogue.comment.entity.CommentLike;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CommentLikeRepository extends JpaRepository<CommentLike, Long> {
    boolean existsByCommentIdAndMemberId(Long commentId, Long memberId);
    Optional<CommentLike> findByCommentIdAndMemberId(Long commentId, Long memberId);
}
