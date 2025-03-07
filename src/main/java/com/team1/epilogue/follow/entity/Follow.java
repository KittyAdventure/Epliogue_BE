package com.team1.epilogue.follow.entity;

import com.team1.epilogue.auth.entity.Member;
import lombok.*;

import jakarta.persistence.*;

/**
 * [클래스 레벨]
 * 회원 간의 팔로우 관계를 나타냄
 *
 * 필드:
 * - id: 팔로우 관계의 고유 식별자
 * - follower: 팔로우 요청을 보낸 회원
 * - followed: 팔로우 당하는 회원
 */
@Entity
@Table(name = "follows", uniqueConstraints = @UniqueConstraint(columnNames = {"follower_id", "followed_id"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Follow {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "follower_id", nullable = false)
    private Member follower;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "followed_id", nullable = false)
    private Member followed;
}
