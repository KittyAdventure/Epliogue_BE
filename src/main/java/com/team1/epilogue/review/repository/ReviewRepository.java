package com.team1.epilogue.review.repository;

import com.team1.epilogue.auth.entity.Member;
import com.team1.epilogue.review.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    /**
     * 특정 책의 ID에 해당하는 리뷰들을 페이징하여 조회합니다
     *
     * @param bookId   조회할 책의 ID
     * @param pageable 페이징 정보를 포함한 객체
     * @return 해당 책의 리뷰 목록 (페이징 적용)
     */
    Page<Review> findByBookId(String bookId, Pageable pageable);

    // 회원 리스트에 포함된 리뷰들을 페이징 조회하는 메서드
    Page<Review> findByMemberIn(Iterable<Member> members, Pageable pageable);
}

