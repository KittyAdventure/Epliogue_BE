package com.team1.epilogue.share.controller;

import com.team1.epilogue.book.repository.BookRepository;
import com.team1.epilogue.review.repository.ReviewRepository;
import com.team1.epilogue.share.dto.KakaoShareRequest;
import com.team1.epilogue.share.dto.KakaoShareResponse;
import com.team1.epilogue.share.dto.ShareResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/share")
public class ShareController {

    private final BookRepository bookRepository;
    private final ReviewRepository reviewRepository;

    public ShareController(BookRepository bookRepository, ReviewRepository reviewRepository) {
        this.bookRepository = bookRepository;
        this.reviewRepository = reviewRepository;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ShareResponse> getShareUrl(
            @RequestParam("type") String type,
            @RequestParam("id") String id) {
        if (type == null || type.trim().isEmpty() || id == null || id.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Required parameter missing or invalid");
        }

        validateResourceExistence(type, id);

        String shareUrl = buildShareUrl(type, id);
        ShareResponse response = new ShareResponse(shareUrl);
        return ResponseEntity.ok(response);
    }

    @PostMapping(value = "/kakao", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<KakaoShareResponse> shareViaKakao(
            @RequestParam("type") String type,
            @RequestParam("id") String id,
            @RequestBody KakaoShareRequest request) {
        if (type == null || type.trim().isEmpty() || id == null || id.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Required parameter missing or invalid");
        }
        if (request.getTargetType() == null || request.getTargetType().trim().isEmpty() ||
                request.getTargetId() == null || request.getTargetId().trim().isEmpty() ||
                request.getRecipient() == null || request.getRecipient().trim().isEmpty() ||
                request.getMessage() == null || request.getMessage().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Required request body parameter missing or invalid");
        }

        validateResourceExistence(type, id);

        String shareUrl = buildShareUrl(type, id);
        KakaoShareResponse response = new KakaoShareResponse(
                "KakaoTalk message sent successfully",
                shareUrl,
                request.getMessage()
        );
        return ResponseEntity.ok(response);
    }

    private void validateResourceExistence(String type, String id) {
        switch (type.toLowerCase()) {
            case "book":
                if (!bookRepository.existsById(id)) {
                    throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Book not found");
                }
                break;
            case "review":
                Long reviewId;
                try {
                    reviewId = Long.parseLong(id);
                } catch (NumberFormatException e) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Review id must be a number");
                }
                if (!reviewRepository.existsById(reviewId)) {
                    throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Review not found");
                }
                break;
            default:
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid type parameter");
        }
    }

    private String buildShareUrl(String type, String id) {
        switch (type.toLowerCase()) {
            case "book":
                return "https://13.125.112.89/books/" + id;
            case "review":
                return "https://13.125.112.89/reviews/" + id;
            default:
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid type parameter");
        }
    }
}
