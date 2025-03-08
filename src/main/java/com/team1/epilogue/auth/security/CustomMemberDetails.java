package com.team1.epilogue.auth.security;

import com.team1.epilogue.auth.entity.Member;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

@Getter
public class CustomMemberDetails implements UserDetails {

    private final Member member;
    private final Long id;
    private final String username;
    private final String password;
    private final Collection<? extends GrantedAuthority> authorities;
    private final String name;
    private final String profileImg;

    public CustomMemberDetails(Member member, Long id, String username, String password,
                               Collection<? extends GrantedAuthority> authorities,
                               String name, String profileImg) {
        this.member = member;
        this.id = id;
        this.username = username;
        this.password = password;
        this.authorities = authorities;
        this.name = name;
        this.profileImg = profileImg;
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
    public static CustomMemberDetails fromMember(Member member) {
        return new CustomMemberDetails(
                member,
                member.getId(),
                member.getLoginId(),
                member.getPassword(),
                Collections.singleton(() -> "ROLE_USER"),
                member.getName(),
                member.getProfileUrl()
        );
    }
}
