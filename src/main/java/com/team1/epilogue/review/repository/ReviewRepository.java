package com.team1.epilogue.review.repository;

import com.team1.epilogue.auth.entity.Member;
import com.team1.epilogue.review.entity.Review;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    @Query("SELECT r FROM Review r JOIN FETCH r.member WHERE r.book.id = :bookId")
    Page<Review> findByBookIdWithMember(@Param("bookId") String bookId, Pageable pageable);

    @Modifying
    @Query("UPDATE Review r SET r.likeCount = r.likeCount + 1 WHERE r.id = :reviewId")
    int increaseLikeCount(@Param("reviewId") Long reviewId);

    @Modifying
    @Query("UPDATE Review r SET r.likeCount = r.likeCount - 1 WHERE r.id = :reviewId AND r.likeCount > 0")
    int decreaseLikeCount(@Param("reviewId") Long reviewId);

    @Modifying
    @Query("UPDATE Review r SET r.commentsCount = r.commentsCount + 1 WHERE r.id = :reviewId")
    int increaseCommentsCount(@Param("reviewId") Long reviewId);

    @Modifying
    @Query("UPDATE Review r SET r.commentsCount = r.commentsCount - 1 WHERE r.id = :reviewId AND r.likeCount > 0")
    int decreaseCommentsCount(@Param("reviewId") Long reviewId);

    @Query("SELECT r FROM Review r JOIN FETCH r.book JOIN FETCH r.member WHERE r.member.id =:memberID")
    Page<Review> findByMemberId(@Param("memberID")String id ,Pageable pageable);

    Page<Review> findByBookId(String bookId, Pageable pageable);

    Page<Review> findByMemberIn(Iterable<Member> members, Pageable pageable);
    Page<Review> findByMemberInWithFetchJoin(@Param("members") Iterable<Member> members, Pageable pageable);

}
