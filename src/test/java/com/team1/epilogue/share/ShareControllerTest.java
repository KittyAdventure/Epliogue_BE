package com.team1.epilogue.share.controller;

import com.team1.epilogue.book.repository.BookRepository;
import com.team1.epilogue.review.repository.ReviewRepository;
import com.team1.epilogue.share.controller.ShareController;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@WithMockUser(username = "user1")
@DisplayName("ShareController 테스트")
public class ShareControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BookRepository bookRepository;

    @MockitoBean
    private ReviewRepository reviewRepository;

    @Test
    @DisplayName("URI 공유 - 책")
    public void testGetShareUrlForBook_Success() throws Exception {
        String bookId = "book123";
        when(bookRepository.existsById(bookId)).thenReturn(true);

        mockMvc.perform(get("/api/share")
                        .param("type", "book")
                        .param("id", bookId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.shareUrl").value("https://13.125.112.89/books/" + bookId));
    }

    @Test
    @DisplayName("URI 공유 - 리뷰")
    public void testGetShareUrlForReview_Success() throws Exception {
        String reviewId = "1";
        when(reviewRepository.existsById(1L)).thenReturn(true);

        mockMvc.perform(get("/api/share")
                        .param("type", "review")
                        .param("id", reviewId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.shareUrl").value("https://13.125.112.89/reviews/" + reviewId));
    }

    @Test
    @DisplayName("URI 공유 - 잘못된 타입")
    public void testGetShareUrl_InvalidType() throws Exception {
        mockMvc.perform(get("/api/share")
                        .param("type", "invalid")
                        .param("id", "123")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("카카오 공유")
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
    @DisplayName("카카오 공유 - 필수 필드 누락")
    public void testShareViaKakao_MissingRequestBodyFields() throws Exception {
        String type = "book";
        String id = "book123";
        when(bookRepository.existsById(id)).thenReturn(true);

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
