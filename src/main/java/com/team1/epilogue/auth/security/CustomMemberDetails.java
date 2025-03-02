package com.team1.epilogue.auth.security;

import com.team1.epilogue.auth.entity.Member;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

/**
 * [클래스 레벨]
 * UserDetails 인터페이스를 구현한 커스텀 사용자 정보 클래스.
 * 인증 및 인가에 필요한 사용자 정보를 제공.
 */
public class CustomMemberDetails implements UserDetails {

    private final Long id;
    private final String username;
    private final String password;
    private final Collection<? extends GrantedAuthority> authorities;
    private final String name;
    private final String profileImg;

    public CustomMemberDetails(Long id, String username, String password,
                               Collection<? extends GrantedAuthority> authorities,
                               String name, String profileImg) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.authorities = authorities;
        this.name = name;
        this.profileImg = profileImg;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getProfileImg() {
        return profileImg;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    /**
     * [메서드 레벨]
     * Member 엔티티를 CustomMemberDetails 객체로 변환하는 정적 팩토리 메서드.
     *
     * @param member Member 엔티티
     * @return CustomMemberDetails 객체
     */
    public static CustomMemberDetails fromMember(Member member) {
        return new CustomMemberDetails(
                member.getId(),
                member.getLoginId(),
                member.getPassword(),
                Collections.singleton(() -> "ROLE_USER"),
                member.getName(),
                member.getProfileUrl()
        );
    }
}
