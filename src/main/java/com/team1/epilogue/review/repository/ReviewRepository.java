package com.team1.epilogue.review.repository;

import com.team1.epilogue.auth.entity.Member;
import com.team1.epilogue.review.entity.Review;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    Page<Review> findByBookId(String bookId, Pageable pageable);

    Page<Review> findByMemberIn(Iterable<Member> members, Pageable pageable);

    @Query("SELECT r FROM Review r JOIN FETCH r.member m WHERE m IN :members")
    Page<Review> findByMemberInWithFetchJoin(@Param("members") Iterable<Member> members, Pageable pageable);

}

