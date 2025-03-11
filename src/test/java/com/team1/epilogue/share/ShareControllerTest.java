package com.team1.epilogue.share.controller;

import com.team1.epilogue.book.repository.BookRepository;
import com.team1.epilogue.review.repository.ReviewRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ShareController.class)
public class ShareControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BookRepository bookRepository;

    @MockitoBean
    private ReviewRepository reviewRepository;

    @Test
    public void testGetShareUrlForBook_Success() throws Exception {
        String bookId = "book123";
        // BookRepository가 해당 ID를 가진 책이 존재한다고 가정
        when(bookRepository.existsById(bookId)).thenReturn(true);

        mockMvc.perform(get("/api/share")
                        .param("type", "book")
                        .param("id", bookId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.shareUrl").value("https://13.125.112.89/books/" + bookId));
    }

    @Test
    public void testGetShareUrlForReview_Success() throws Exception {
        String reviewId = "1";
        // reviewRepository의 ID는 Long 타입이므로 변환 후 존재하는 것으로 가정
        when(reviewRepository.existsById(1L)).thenReturn(true);

        mockMvc.perform(get("/api/share")
                        .param("type", "review")
                        .param("id", reviewId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.shareUrl").value("https://13.125.112.89/reviews/" + reviewId));
    }

    @Test
    public void testGetShareUrl_InvalidType() throws Exception {
        mockMvc.perform(get("/api/share")
                        .param("type", "invalid")
                        .param("id", "123")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testShareViaKakao_Success() throws Exception {
        String type = "book";
        String id = "book123";
        when(bookRepository.existsById(id)).thenReturn(true);

        String requestJson = "{"
                + "\"targetType\":\"book\","
                + "\"targetId\":\"book123\","
                + "\"recipient\":\"user@example.com\","
                + "\"message\":\"Hello\""
                + "}";

        mockMvc.perform(post("/api/share/kakao")
                        .param("type", type)
                        .param("id", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("KakaoTalk message sent successfully"))
                .andExpect(jsonPath("$.shareUrl").value("https://13.125.112.89/books/" + id))
                .andExpect(jsonPath("$.message").value("Hello"));
    }

    @Test
    public void testShareViaKakao_MissingRequestBodyFields() throws Exception {
        String type = "book";
        String id = "book123";
        when(bookRepository.existsById(id)).thenReturn(true);

        // 필수 필드들이 누락된 요청 JSON
        String requestJson = "{}";

        mockMvc.perform(post("/api/share/kakao")
                        .param("type", type)
                        .param("id", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}
