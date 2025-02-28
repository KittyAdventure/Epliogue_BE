package com.team1.epilogue.review.controller;

import com.team1.epilogue.auth.entity.Member;
import com.team1.epilogue.review.dto.ReviewRequestDto;
import com.team1.epilogue.review.dto.ReviewResponseDto;
import com.team1.epilogue.review.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
    @PostMapping("/book/{bookId}/reviews")
    public ResponseEntity<ReviewResponseDto> createReview(@PathVariable Long bookId,
                                                          @RequestBody ReviewRequestDto reviewRequestDto,
                                                          Authentication authentication) {
        Member member = (Member) authentication.getPrincipal(); // Authentication 에서 Member 객체 추출
        ReviewResponseDto reviewResponseDto =
                reviewService.createReview(bookId, reviewRequestDto, member);

        return ResponseEntity.ok().body(reviewResponseDto);
    }

    /**
     * 특정 책의 모든 리뷰를 페이징하여 조회합니다
     * - 기본 정렬: 최신순 (createdAt DESC)
     * - 향후 좋아요순(likesCount DESC) 정렬 기능 추가 예정
     *
     * @param bookId 조회할 책의 ID
     * @param page   조회할 페이지 번호 (1부터 시작)
     * @param size   한 페이지당 조회할 리뷰 개수
     * @return 해당 책의 리뷰 목록을 담은 페이징된 DTO 리스트
     */
    @GetMapping("/book/{bookId}/reviews")
    public ResponseEntity<Page<ReviewResponseDto>> getReviews(@PathVariable Long bookId,
                                                              @RequestParam("page") int page,
                                                              @RequestParam("size") int size) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<ReviewResponseDto> reviews = reviewService.getReviews(bookId, pageable);

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
     * @param authentication   현재 인증된 사용자
     * @return 수정된 리뷰의 상세 정보를 담은 DTO
     */
    @PutMapping("/review/{reviewId}")
    public ResponseEntity<ReviewResponseDto> updateReview(@PathVariable Long reviewId,
                                                          @RequestBody ReviewRequestDto reviewRequestDto,
                                                          Authentication authentication) {
        Member member = (Member) authentication.getPrincipal();
        ReviewResponseDto updatedReview = reviewService.updateReview(reviewId, reviewRequestDto, member);

        return ResponseEntity.ok().body(updatedReview);
    }

    /**
     * 특정 리뷰를 삭제합니다
     *
     * @param reviewId       삭제할 리뷰의 ID
     * @param authentication 현재 인증된 사용자
     * @return 삭제 완료 메시지
     */
    @DeleteMapping("/review/{reviewId}")
    public ResponseEntity<String> deleteReview(@PathVariable Long reviewId,
                                               Authentication authentication) {
        Member member = (Member) authentication.getPrincipal();
        reviewService.deleteReview(reviewId, member);

        return ResponseEntity.ok().body("리뷰가 성공적으로 삭제되었습니다.");
    }
}
