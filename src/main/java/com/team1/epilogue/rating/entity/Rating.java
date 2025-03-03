package com.team1.epilogue.rating.entity;

import com.team1.epilogue.auth.entity.Member;
import com.team1.epilogue.book.entity.Book;
import com.team1.epilogue.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder

@Entity
@EntityListeners(AuditingEntityListener.class)
public class Rating extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;    // 별점 ID

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;  // 해당 별점이 속한 책

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;  // 별점을 작성한 사용자

    private Double score;   // 별점 (0.5 ~ 5)

    /**
     * 별점을 업데이트합니다
     *
     * @param score 새로운 별점
     */
    public void updateScore(Double score) {
        this.score = score;
    }
}
