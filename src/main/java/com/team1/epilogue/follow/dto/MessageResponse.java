package com.team1.epilogue.follow.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * [클래스 레벨]
 * 메시지 응답을 위한 DTO.
 *
 * 필드:
 * - message: 응답 메시지 (예: "성공", "실패" 등)
 */
@Data
@AllArgsConstructor
public class MessageResponse {
    private String message;
}
