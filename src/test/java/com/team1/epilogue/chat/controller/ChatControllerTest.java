package com.team1.epilogue.chat.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.team1.epilogue.chat.dto.ChatMessageDto;
import com.team1.epilogue.chat.service.ChatMessageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.AutoConfigureDataMongo;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.convert.MongoConverter;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;


@ExtendWith(SpringExtension.class) // JUnit 5 환경에서 테스트 실행
@WebMvcTest(ChatController.class) // ChatController에 대한 웹 계층 테스트
@AutoConfigureDataMongo
class ChatControllerTest {

  @Autowired
  private MockMvc mockMvc; // MockMvc를 사용하여 컨트롤러 테스트

  @MockitoBean
  private ChatMessageService chatMessageService; // ChatMessageService를 Mock 처리하여 테스트

  private ChatMessageDto chatMessageDto;

  @Autowired
  private ObjectMapper objectMapper; // JSON 변환을 위한 ObjectMapper 추가

  @MockitoBean
  private MongoTemplate mongoTemplate;

  @MockitoBean
  private MongoConverter mongoConverter;


  @BeforeEach
  void setUp() {
    // 테스트 실행 전 샘플 메시지 DTO 생성
    chatMessageDto = ChatMessageDto.builder()
        .roomId("room1")
        .memberId(1L)
        .content("Hello World")
        .build();
  }

  @Test
  void saveMessage_ShouldReturnCreated() throws Exception {
    // chatMessageService.saveMessage()가 호출되었을 때 chatMessageDto를 반환하도록 설정
    when(chatMessageService.saveMessage(any(ChatMessageDto.class))).thenReturn(chatMessageDto);

    // API 호출 및 검증
    mockMvc.perform(post("/api/meeting/chat/rooms/room1/messages")
            .param("memberId", "1") // 요청 파라미터 추가
            .param("content", "Hello World")
            .contentType(MediaType.APPLICATION_JSON)) // JSON 요청 설정
        .andExpect(status().isCreated()) // HTTP 상태 코드 201 확인
        .andExpect(jsonPath("$.roomId").value("room1")) // JSON 응답값 검증
        .andExpect(jsonPath("$.memberId").value(1L))
        .andExpect(jsonPath("$.content").value("Hello World"));
  }



}