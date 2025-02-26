package com.team1.epilogue.auth.security;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CustomMemberDetails implements UserDetails {

  private Long id;  // 사용자의 고유 ID
  private String username;  // 사용자 이름 (로그인 ID)
  private String password;  // 사용자 비밀번호
  private Collection<? extends GrantedAuthority> authorities;  // 사용자 권한

  // UserDetails 인터페이스 구현
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
    return true; // 계정 만료 여부 (true = 만료되지 않음)
  }

  @Override
  public boolean isAccountNonLocked() {
    return true; // 계정 잠금 여부 (true = 잠금되지 않음)
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true; // 자격 증명 만료 여부 (true = 만료되지 않음)
  }

  @Override
  public boolean isEnabled() {
    return true; // 사용자 활성화 여부 (true = 활성화됨)
  }
}