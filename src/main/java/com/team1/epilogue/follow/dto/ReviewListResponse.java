package com.team1.epilogue.follow.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.List;

@Data
@AllArgsConstructor
public class ReviewListResponse {
    private List<ReviewDto> review;
    private PaginationDto pagination;
}
