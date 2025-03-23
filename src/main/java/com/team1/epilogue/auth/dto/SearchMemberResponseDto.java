package com.team1.epilogue.auth.dto;

import com.team1.epilogue.auth.entity.Member;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class SearchMemberResponseDto {
  private String loginId;
  private String nickname;
  private String email;
  private String profileUrl;
  private LocalDateTime createdAt;

  public static SearchMemberResponseDto fromEntity(Member member) {
    return new SearchMemberResponseDto(
        member.getLoginId(),
        member.getNickname(),
        member.getEmail(),
        member.getProfileUrl(),
        member.getCreatedAt()
    );

  }

}
