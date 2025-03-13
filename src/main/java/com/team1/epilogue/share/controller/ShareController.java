package com.team1.epilogue.share.controller;

import com.team1.epilogue.book.repository.BookRepository;
import com.team1.epilogue.review.repository.ReviewRepository;
import com.team1.epilogue.share.dto.ShareResponse;
import org.springframework.beans.factory.annotation.Value;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/share")
@RequiredArgsConstructor
public class ShareController {

    private final BookRepository bookRepository;
    private final ReviewRepository reviewRepository;
    @Value("${baseIp}")
    private String baseIp;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ShareResponse> getShareUrl(
            @RequestParam("type") String type,
            @RequestParam("id") String id) {
        if (type == null || type.trim().isEmpty() || id == null || id.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "필수 파라미터가 누락되었거나 올바르지 않습니다.");
        }

        validateResourceExistence(type, id);
        String shareUrl = buildShareUrl(type, id);
        ShareResponse response = new ShareResponse(shareUrl);
        return ResponseEntity.ok(response);
    }

    private void validateResourceExistence(String type, String id) {
        switch (type.toLowerCase()) {
            case "book":
                if (!bookRepository.existsById(id)) {
                    throw new ResponseStatusException(HttpStatus.NOT_FOUND, "책을 찾을 수 없습니다.");
                }
                break;
            case "review":
                Long reviewId;
                try {
                    reviewId = Long.parseLong(id);
                } catch (NumberFormatException e) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "리뷰 id는 숫자여야 합니다.");
                }
                if (!reviewRepository.existsById(reviewId)) {
                    throw new ResponseStatusException(HttpStatus.NOT_FOUND, "리뷰를 찾을 수 없습니다.");
                }
                break;
            default:
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "잘못된 type 파라미터입니다.");
        }
    }

    private String buildShareUrl(String type, String id) {
        switch (type.toLowerCase()) {
            case "book":
                return baseIp + "/books/" + id;
            case "review":
                return baseIp + "/reviews/" + id;
            default:
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "잘못된 type 파라미터입니다.");
        }
    }
}


