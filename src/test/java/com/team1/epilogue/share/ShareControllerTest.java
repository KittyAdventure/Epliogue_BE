package com.team1.epilogue.share;

import com.team1.epilogue.book.repository.BookRepository;
import com.team1.epilogue.review.repository.ReviewRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@DisplayName("ShareController 테스트")
public class ShareControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BookRepository bookRepository;

    @MockitoBean
    private ReviewRepository reviewRepository;

    @Test
    @DisplayName("책 공유 URL 생성 성공")
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
    @DisplayName("리뷰 공유 URL 생성 성공")
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
    @DisplayName("잘못된 타입 요청 시 400 Bad Request")
    public void testGetShareUrl_InvalidType() throws Exception {
        mockMvc.perform(get("/api/share")
                        .param("type", "invalid")
                        .param("id", "123")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}
