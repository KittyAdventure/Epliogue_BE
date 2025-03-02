package com.team1.epilogue.rating.repository;

import com.team1.epilogue.rating.entity.Rating;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface RatingRepository extends JpaRepository<Rating, Long> {

    /**
     * 특정 책에 대해 특정 사용자가 작성한 별점을 조회합니다
     *
     * @param memberId 책에 별점을 남긴 사용자의 ID
     * @param bookId   해당 책의 ID
     * @return 해당 책에 사용자가 남긴 별점 정보가 담긴 Optional 객체
     */
    Optional<Rating> findByMemberIdAndBookId(Long memberId, String bookId);
}
