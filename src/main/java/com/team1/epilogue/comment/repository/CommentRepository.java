package com.team1.epilogue.comment.repository;

import com.team1.epilogue.auth.entity.Member;
import com.team1.epilogue.comment.entity.Comment;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<Comment,Long> {
  @Query("SELECT c FROM Comment c JOIN FETCH c.review r JOIN FETCH r.book WHERE c.member = :member")
  Page<Comment> findAllByMemberId(Pageable page, Member member);

  List<Comment> findAllByMember(Member member);

}
