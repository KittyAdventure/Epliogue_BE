package com.team1.epilogue.review.controller;

import com.team1.epilogue.auth.entity.Member;
import com.team1.epilogue.review.dto.ReviewRequestDto;
import com.team1.epilogue.review.dto.ReviewResponseDto;
import com.team1.epilogue.review.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ReviewController {

    private final ReviewService reviewService;

    /**
     * 특정 책에 대한 리뷰를 생성합니다
     *
     * @param bookId           리뷰를 작성할 책의 ID
     * @param reviewRequestDto 클라이언트가 전달한 리뷰 데이터
     * @param member           현재 인증된 사용자
     * @return 생성된 리뷰의 상세 정보를 담은 DTO
     */
    @PostMapping("/book/{bookId}/reviews")
    public ResponseEntity<ReviewResponseDto> createReview(@PathVariable Long bookId,
                                                          @RequestBody ReviewRequestDto reviewRequestDto,
                                                          @AuthenticationPrincipal Member member) {
        ReviewResponseDto reviewResponseDto =
                reviewService.createReview(bookId, reviewRequestDto, member);

        return ResponseEntity.ok().body(reviewResponseDto);
    }

    /**
     * 특정 책의 모든 리뷰를 조회합니다
     *
     * @param bookId 리뷰를 조회할 책의 ID
     * @return 해당 책의 리뷰 목록을 담은 DTO 리스트
     */
    @GetMapping("/book/{bookId}/reviews")
    public ResponseEntity<List<ReviewResponseDto>> getReviews(@PathVariable Long bookId) {
        List<ReviewResponseDto> reviews = reviewService.getReviews(bookId);

        return ResponseEntity.ok().body(reviews);
    }

    /**
     * 특정 리뷰의 상세 정보를 조회합니다
     *
     * @param reviewId 조회할 리뷰의 ID
     * @return 해당 리뷰의 상세 정보를 담은 DTO
     */
    @GetMapping("/review/{reviewId}")
    public ResponseEntity<ReviewResponseDto> getReview(@PathVariable Long reviewId) {
        ReviewResponseDto reviewResponseDto = reviewService.getReview(reviewId);

        return ResponseEntity.ok().body(reviewResponseDto);
    }

    /**
     * 특정 리뷰를 수정합니다
     *
     * @param reviewId         수정할 리뷰의 ID
     * @param reviewRequestDto 클라이언트가 전달한 수정 데이터
     * @param member           현재 인증된 사용자
     * @return 수정된 리뷰의 상세 정보를 담은 DTO
     */
    @PutMapping("/review/{reviewId}")
    public ResponseEntity<ReviewResponseDto> updateReview(@PathVariable Long reviewId,
                                                          @RequestBody ReviewRequestDto reviewRequestDto,
                                                          @AuthenticationPrincipal Member member) {
        ReviewResponseDto updatedReview = reviewService.updateReview(reviewId, reviewRequestDto, member);

        return ResponseEntity.ok().body(updatedReview);
    }

    /**
     * 특정 리뷰를 삭제합니다
     *
     * @param reviewId 삭제할 리뷰의 ID
     * @param member   현재 인증된 사용자
     * @return 삭제 완료 메시지
     */
    @DeleteMapping("/review/{reviewId}")
    public ResponseEntity<String> deleteReview(@PathVariable Long reviewId,
                                               @AuthenticationPrincipal Member member) {
        reviewService.deleteReview(reviewId, member);

        return ResponseEntity.ok().body("리뷰가 성공적으로 삭제되었습니다.");
    }
}
