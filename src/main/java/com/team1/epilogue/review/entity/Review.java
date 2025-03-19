package com.team1.epilogue.review.entity;

import com.team1.epilogue.auth.entity.Member;
import com.team1.epilogue.book.entity.Book;
import com.team1.epilogue.common.entity.BaseEntity;
import com.team1.epilogue.review.service.StringListConverter;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@EntityListeners(AuditingEntityListener.class)
public class Review extends BaseEntity {

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

    @Builder.Default
    @Convert(converter = StringListConverter.class) //  변환기 적용
    @Column(columnDefinition = "TEXT") //  길이 제한 없음
    private List<String> imageUrls = new ArrayList<>();

    @Builder.Default
    @Column(nullable = false)
    private int likeCount = 0;

    @Builder.Default
    @Column(nullable = false)
    private int commentsCount = 0; // 댓글 갯수

    /**
     * 리뷰 내용을 수정합니다
     *
     * @param content 수정할 새로운 내용
     */
    public void updateReview(String content) {
        this.content = content;
    }

    public void updateImageUrls(List<String> imageUrls) {
        this.imageUrls = imageUrls;
    }
}

