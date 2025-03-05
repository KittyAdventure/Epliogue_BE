package com.team1.epilogue.follow.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.List;

@Data
@AllArgsConstructor
public class MembersResponse {
    private List<MemberDto> members;
}
