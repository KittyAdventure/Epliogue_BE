package com.team1.epilogue.follow.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class MembersResponse {
    private List<MemberDto> members;
}
