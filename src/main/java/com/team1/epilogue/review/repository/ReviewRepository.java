package com.team1.epilogue.review.repository;

import com.team1.epilogue.auth.entity.Member;
import com.team1.epilogue.review.entity.Review;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    // 특정 책의 리뷰 전체 조회 (Member, Book 함께 조회, 페이징 적용)
    @Query("SELECT r FROM Review r JOIN FETCH r.member JOIN FETCH r.book WHERE r.book.id = :bookId")
    Page<Review> findByBookIdWithMember(@Param("bookId") String bookId, Pageable pageable);

    // 특정 리뷰 상세 조회 (Member, Book 함께 조회)
    @Query("SELECT r FROM Review r JOIN FETCH r.member JOIN FETCH r.book WHERE r.id = :reviewId")
    Optional<Review> findByIdWithBookAndMember(@Param("reviewId") Long reviewId);

    // 최신 리뷰 목록 조회 (Member, Book 함께 조회, 최신순 정렬, 페이징 적용)
    @Query("SELECT r FROM Review r JOIN FETCH r.book JOIN FETCH r.member ORDER BY r.createdAt DESC")
    Page<Review> findAllReviewsSortedByLatest(Pageable pageable);

    // 리뷰 좋아요 증가 (좋아요 개수를 직접 업데이트)
    @Modifying
    @Query("UPDATE Review r SET r.likeCount = r.likeCount + 1 WHERE r.id = :reviewId")
    int increaseLikeCount(@Param("reviewId") Long reviewId);

    // 리뷰 좋아요 감소 (최소 0 유지, 직접 업데이트)
    @Modifying
    @Query("UPDATE Review r SET r.likeCount = r.likeCount - 1 WHERE r.id = :reviewId AND r.likeCount > 0")
    int decreaseLikeCount(@Param("reviewId") Long reviewId);

    @Query("SELECT r FROM Review r JOIN FETCH r.book JOIN FETCH r.member "
            + "WHERE r.createdAt BETWEEN :startDate AND :endDate AND r.member.loginId = :memberId")
    List<Review> findByDateAndMember(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("memberId") String memberId);

    Page<Review> findByBookId(String bookId, Pageable pageable);

    @Query("SELECT r FROM Review r JOIN FETCH r.member m JOIN FETCH r.book b " +
            "WHERE r.book.id = :bookId AND m IN :members")
    Page<Review> findByBookIdAndMemberInWithFetchJoin(@Param("bookId") String bookId,
                                                      @Param("members") Iterable<Member> members,
                                                      Pageable pageable);

    @Query("SELECT r FROM Review r JOIN FETCH r.member m WHERE m IN :members")
    Page<Review> findByMemberInWithFetchJoin(@Param("members") Iterable<Member> members,
                                             Pageable pageable);

    @Modifying
    @Query("UPDATE Review r SET r.commentsCount = r.commentsCount + 1 WHERE r.id = :reviewId")
    int increaseCommentsCount(@Param("reviewId") Long reviewId);

    @Modifying
    @Query("UPDATE Review r SET r.commentsCount = r.commentsCount - 1 WHERE r.id = :reviewId AND r.commentsCount > 0")
    int decreaseCommentsCount(@Param("reviewId") Long reviewId);

    @Query("SELECT r FROM Review r JOIN FETCH r.book JOIN FETCH r.member WHERE r.member.id =:memberID")
    Page<Review> findByMemberId(@Param("memberID") String id, Pageable pageable);

    @Query("SELECT r FROM Review r JOIN FETCH r.member m JOIN FETCH r.book b WHERE b.id = :bookId AND m IN :members")
    Page<Review> findFriendReviewsByBookIdAndMemberIn(@Param("bookId") String bookId,
                                                      @Param("members") Iterable<Member> members,
                                                      Pageable pageable);

}
