package com.team1.epilogue.review.entity;

import com.team1.epilogue.auth.entity.Member;
import com.team1.epilogue.book.Book;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder

@Entity
@EntityListeners(AuditingEntityListener.class)
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 리뷰 작성자
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    // 해당 리뷰가 속한 책
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    // 리뷰 내용
    private String content;

    // 리뷰에 첨부된 이미지 URL
    @Column(name = "image_url")
    private String imageUrl;

    // 생성 시간
    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    // 수정 시간
    @LastModifiedDate
    @Column(name = "modified_at")
    private LocalDateTime modifiedAt;

    /**
     * 리뷰 내용을 수정합니다
     *
     * @param content 수정할 새로운 내용
     */
    public void updateReview(String content) {
        this.content = content;
    }
}

