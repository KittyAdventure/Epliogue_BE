package com.team1.epilogue.review.controller;

import com.team1.epilogue.auth.entity.Member;
import com.team1.epilogue.review.dto.ReviewRequestDto;
import com.team1.epilogue.review.dto.ReviewResponseDto;
import com.team1.epilogue.review.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

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
     * @param authentication   현재 인증된 사용자
     * @return 생성된 리뷰의 상세 정보를 담은 DTO
     */
    @PostMapping("/books/{bookId}/reviews")
    public ResponseEntity<ReviewResponseDto> createReview(@PathVariable String bookId,
                                                          @RequestBody ReviewRequestDto reviewRequestDto,
                                                          Authentication authentication) {
        Member member = (Member) authentication.getPrincipal(); // Authentication 에서 Member 객체 추출
        ReviewResponseDto reviewResponseDto =
                reviewService.createReview(bookId, reviewRequestDto, member);

        return ResponseEntity.ok(reviewResponseDto);
    }

    /**
     * 특정 책의 모든 리뷰를 페이징하여 조회합니다
     * - 기본 정렬: 좋아요순 (likeCount DESC)
     * - 정렬 방식 선택 가능: 최신순 (latest), 좋아요순 (likes)
     *
     * @param bookId   조회할 책의 ID
     * @param page     조회할 페이지 번호 (1부터 시작)
     * @param size     한 페이지당 조회할 리뷰 개수
     * @param sortType 정렬 기준 ("likes"=좋아요순, "latest"=최신순, 기본값: "likes")
     * @return 해당 책의 리뷰 목록을 담은 페이징된 DTO 리스트
     */
    @GetMapping("/books/{bookId}/reviews")
    public ResponseEntity<Page<ReviewResponseDto>> getReviews(
            @PathVariable String bookId,
            @RequestParam("page") int page,
            @RequestParam("size") int size,
            @RequestParam(value = "sortType", defaultValue = "likes") String sortType
    ) {
        Page<ReviewResponseDto> reviews = reviewService.getReviews(bookId, page, size, sortType);

        return ResponseEntity.ok(reviews);
    }

    /**
     * 특정 리뷰의 상세 정보를 조회합니다
     *
     * @param reviewId 조회할 리뷰의 ID
     * @return 해당 리뷰의 상세 정보를 담은 DTO
     */
    @GetMapping("/reviews/{reviewId}")
    public ResponseEntity<ReviewResponseDto> getReviewDetail(@PathVariable Long reviewId) {
        ReviewResponseDto reviewResponseDto = reviewService.getReviewDetail(reviewId);

        return ResponseEntity.ok(reviewResponseDto);
    }

    /**
     * 특정 리뷰를 수정합니다
     *
     * @param reviewId         수정할 리뷰의 ID
     * @param reviewRequestDto 클라이언트가 전달한 수정 데이터
     * @param authentication   현재 인증된 사용자
     * @return 수정된 리뷰의 상세 정보를 담은 DTO
     */
    @PutMapping("/reviews/{reviewId}")
    public ResponseEntity<ReviewResponseDto> updateReview(@PathVariable Long reviewId,
                                                          @RequestBody ReviewRequestDto reviewRequestDto,
                                                          Authentication authentication) {
        Member member = (Member) authentication.getPrincipal();
        ReviewResponseDto updatedReview = reviewService.updateReview(reviewId, reviewRequestDto, member);

        return ResponseEntity.ok(updatedReview);
    }

    /**
     * 특정 리뷰를 삭제합니다
     *
     * @param reviewId       삭제할 리뷰의 ID
     * @param authentication 현재 인증된 사용자
     * @return 삭제 완료 메시지
     */
    @DeleteMapping("/reviews/{reviewId}")
    public ResponseEntity<String> deleteReview(@PathVariable Long reviewId,
                                               Authentication authentication) {
        Member member = (Member) authentication.getPrincipal();
        reviewService.deleteReview(reviewId, member);

        return ResponseEntity.ok("리뷰가 성공적으로 삭제되었습니다.");
    }

    @PostMapping("/reviews/{reviewId}/likes")
    public ResponseEntity<String> likeReview(@PathVariable Long reviewId,
                                             Authentication authentication) {
        Member member = (Member) authentication.getPrincipal();
        reviewService.likeReview(reviewId, member);

        return ResponseEntity.ok("좋아요 성공");
    }

    @DeleteMapping("/reviews/{reviewId}/likes")
    public ResponseEntity<String> unlikeReview(@PathVariable Long reviewId,
                                               Authentication authentication) {
        Member member = (Member) authentication.getPrincipal();
        reviewService.unlikeReview(reviewId, member);
        return ResponseEntity.ok("좋아요 취소 성공");
    }
}
