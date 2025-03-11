package com.team1.epilogue.comment.repository;

import com.team1.epilogue.auth.entity.Member;
import com.team1.epilogue.comment.entity.Comment;
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

  @Modifying
  @Query("UPDATE Comment c SET c.likeCount = c.likeCount + 1 WHERE c.id = :commentId")
  int increaseLikeCount(@Param("commentId") Long commentId);

  @Modifying
  @Query("UPDATE Comment c SET c.likeCount = c.likeCount - 1 WHERE c.id = :commentId AND c.likeCount > 0")
  int decreaseLikeCount(@Param("commentId") Long commentId);
}
