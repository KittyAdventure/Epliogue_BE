package com.team1.epilogue.follow.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.team1.epilogue.follow.dto.FollowActionResponse;
import com.team1.epilogue.follow.dto.MembersResponse;
import com.team1.epilogue.follow.dto.MessageResponse;
import com.team1.epilogue.follow.dto.ReviewListResponse;
import com.team1.epilogue.follow.service.FollowService;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = FollowController.class)
@DisplayName("FollowController 테스트")
@RequiredArgsConstructor
public class FollowControllerTest {

    private MockMvc mockMvc;
    @MockitoBean
    private FollowService followService;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @WithMockUser(username = "user1")
    @DisplayName("팔로우 등록 API 테스트 - 성공")
    void testFollowUser() throws Exception {
        FollowActionResponse response = new FollowActionResponse("팔로우 생성 성공", "user1", "user2");
        when(followService.followUser(eq("user1"), eq("user2"))).thenReturn(response);

        mockMvc.perform(post("/api/follows/user2").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is("팔로우 생성 성공")))
                .andExpect(jsonPath("$.followerLoginId", is("user1")))
                .andExpect(jsonPath("$.followedLoginId", is("user2")));
    }

    @Test
    @WithMockUser(username = "user1")
    @DisplayName("팔로우 삭제 API 테스트 - 성공")
    void testUnfollowUser() throws Exception {
        MessageResponse response = new MessageResponse("팔로우 삭제 성공");
        Mockito.doNothing().when(followService).unfollowUser(eq("user1"), eq("user2"));

        mockMvc.perform(delete("/api/follows/user2").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is("팔로우 삭제 성공")));
    }

    @Test
    @WithMockUser(username = "user1")
    @DisplayName("팔로잉 목록 조회 API 테스트 - 성공")
    void testGetFollowList_following() throws Exception {
        when(followService.getFollowingList(eq("user1"))).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/follows/user1")
                        .param("type", "following"))
                .andExpect(status().isOk());
    }


    @Test
    @WithMockUser(username = "user1")
    @DisplayName("팔로우한 회원의 리뷰 조회 API 테스트 - 성공")
    void testGetFollowedReviews() throws Exception {
        ReviewListResponse response = new ReviewListResponse(Collections.emptyList(), null);
        when(followService.getFollowedReviews(eq("user1"), anyInt(), anyInt(), anyString())).thenReturn(response);

        mockMvc.perform(get("/api/follows/review")
                        .param("page", "1")
                        .param("limit", "10")
                        .param("sort", "desc"))
                .andExpect(status().isOk());
    }
}