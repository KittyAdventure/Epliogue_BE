package com.team1.epilogue.comment.repository;

import com.team1.epilogue.auth.entity.Member;
import com.team1.epilogue.comment.entity.Comment;
import com.team1.epilogue.review.entity.Review;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<Comment,Long> {
  @Query("SELECT c FROM Comment c JOIN FETCH c.review r JOIN FETCH r.book WHERE c.member = :member")
  Page<Comment> findAllByMemberId(Pageable page, Member member);

  @Query("SELECT c FROM Comment c JOIN FETCH c.member JOIN FETCH c.review"
      + " WHERE c.review = :review")
  Page<Comment> findCommentsByReviewSortDate(Pageable pageable, Review review);

  // 좋아요 순은 아직 주석처리. 댓글 좋아요 부분 완료되면 다시 작업
//  @Query("SELECT c FROM Comment c JOIN FETCH c.member JOIN FETCH c.review"
//      + " WHERE c.review = :review ORDER BY c.likes DESC")
//  Page<Comment> findCommentsByReviewSortLike(Pageable pageable, Review review);

  @Modifying
  @Query("UPDATE Comment c SET c.likeCount = c.likeCount + 1 WHERE c.id = :commentId")
  int increaseLikeCount(@Param("commentId") Long commentId);

  @Modifying
  @Query("UPDATE Comment c SET c.likeCount = c.likeCount - 1 WHERE c.id = :commentId AND c.likeCount > 0")
  int decreaseLikeCount(@Param("commentId") Long commentId);
}
