package com.team1.epilogue.follow.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PaginationDto {
    private int page;
    private int limit;
    private long total;
}
