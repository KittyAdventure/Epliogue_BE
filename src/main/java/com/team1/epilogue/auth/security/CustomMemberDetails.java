package com.team1.epilogue.auth.security;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

/**
 * [클래스 레벨]
 * Spring Security의 UserDetails 인터페이스를 구현한 커스텀 사용자 정보 클래스
 * 사용자 ID, 이름, 비밀번호 및 권한을 포함하여 인증 및 인가 작업에 사용
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CustomMemberDetails implements UserDetails {

    /**
     * [필드 레벨]
     * id: 사용자의 고유 ID
     * username: 사용자 로그인 ID
     * password: 사용자 비밀번호
     * authorities: 사용자의 권한 목록
     */
    private Long id;
    private String username;
    private String password;
    private Collection<? extends GrantedAuthority> authorities;

    // UserDetails 인터페이스 구현

    /**
     * [메서드 레벨]
     * getAuthorities: 사용자의 권한을 반환하는 메서드
     * @return 사용자의 권한 목록
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    /**
     * [메서드 레벨]
     * getPassword: 사용자의 비밀번호를 반환하는 메서드
     * @return 사용자 비밀번호
     */
    @Override
    public String getPassword() {
        return password;
    }

    /**
     * [메서드 레벨]
     * getUsername: 사용자의 로그인 ID를 반환하는 메서드
     * @return 사용자 로그인 ID
     */
    @Override
    public String getUsername() {
        return username;
    }

    /**
     * [메서드 레벨]
     * isAccountNonExpired: 계정 만료 여부를 반환하는 메서드
     * @return 계정이 만료되지 않은 경우 true
     */
    @Override
    public boolean isAccountNonExpired() {
        return true; // 계정 만료 여부 (true = 만료되지 않음)
    }

    /**
     * [메서드 레벨]
     * isAccountNonLocked: 계정 잠금 여부를 반환하는 메서드
     * @return 계정이 잠금되지 않은 경우 true
     */
    @Override
    public boolean isAccountNonLocked() {
        return true; // 계정 잠금 여부 (true = 잠금되지 않음)
    }

    /**
     * [메서드 레벨]
     * isCredentialsNonExpired: 자격 증명 만료 여부를 반환하는 메서드
     * @return 자격 증명이 만료되지 않은 경우 true
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true; // 자격 증명 만료 여부 (true = 만료되지 않음)
    }

    /**
     * [메서드 레벨]
     * isEnabled: 사용자의 활성화 여부를 반환하는 메서드
     * @return 사용자가 활성화된 경우 true
     */
    @Override
    public boolean isEnabled() {
        return true; // 사용자 활성화 여부 (true = 활성화됨)
    }
}