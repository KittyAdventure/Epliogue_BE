package com.team1.epilogue.auth.entity;

import com.team1.epilogue.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;

@Entity
@Table(name = "member")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class Member extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "login_id", nullable = false, unique = true)
    private String loginId;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String nickname;

    @Column(nullable = false)
    private String name;

    @Column(name = "birth_date", nullable = true)
    private LocalDate birthDate;

    @Column(nullable = true, unique = true)
    private String email;

    @Column(nullable = true)
    private String phone;

    @Column(name = "profile_url")
    private String profileUrl;

    @Column(nullable = true)
    private int point;

    @Column
    private String social;
}
