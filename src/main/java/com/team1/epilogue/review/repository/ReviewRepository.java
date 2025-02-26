package com.team1.epilogue.review.repository;

import com.team1.epilogue.review.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    /**
     * 특정 책의 ID에 해당하는 리뷰들을 조회합니다
     *
     * @param bookId 조회할 책의 ID
     * @return 해당 책에 속한 리뷰 목록
     */
    List<Review> findByBookId(Long bookId);
}
