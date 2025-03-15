package com.team1.epilogue.auth.security;

import com.team1.epilogue.auth.entity.Member;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

@Getter
@RequiredArgsConstructor
public class CustomMemberDetails implements UserDetails {

    private final Long id;
    private final String username;
    private final String password;
    private final Collection<GrantedAuthority> authorities;
    private final String name;
    private final String profileImg;

    @Override
    public Collection<GrantedAuthority> getAuthorities() {
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
                member.getId(),
                member.getLoginId(),
                member.getPassword(),
                Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")),
                member.getName(),
                member.getProfileUrl()
        );
    }
}
