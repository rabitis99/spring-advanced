package org.example.expert.domain.comment.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.expert.common.auth.dto.AuthUser;
import org.example.expert.common.enums.UserRole;
import org.example.expert.common.jwt.JwtUtil;
import org.example.expert.domain.comment.dto.request.CommentSaveRequest;
import org.example.expert.domain.comment.dto.response.CommentSaveResponse;
import org.example.expert.domain.comment.service.CommentService;
import org.example.expert.domain.user.dto.response.UserResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@SpringBootTest
@AutoConfigureMockMvc
class CommentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CommentService commentService;
    @Autowired
    private JwtUtil jwtUtil;

    @Test
    void 댓글_저장_성공() throws Exception {
        // given
        UserResponse userResponse =new UserResponse(1L,"email@email.com");
        long todoId = 1L;

        given(commentService.saveComment(any(AuthUser.class), eq(todoId), any(CommentSaveRequest.class)))
                .willReturn(new CommentSaveResponse(100L, "댓글 내용입니다.", userResponse));

        // given
        String token = jwtUtil.createToken(1L, "test@example.com", UserRole.USER);
        CommentSaveRequest request = new CommentSaveRequest("댓글 내용입니다.");

        // when & then
        mockMvc.perform(post("/todos/1/comments")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(100L))
                .andExpect(jsonPath("$.contents").value("댓글 내용입니다."))
                .andExpect(jsonPath("$.user.id").value(1))
                .andExpect(jsonPath("$.user.email").value("email@email.com"));


        // verify
        verify(commentService).saveComment(any(AuthUser.class), eq(todoId), any(CommentSaveRequest.class));
    }
    @Test
    void 댓글_저장_실패_null_case() throws Exception {

        // given
        String token = jwtUtil.createToken(1L, "test@example.com", UserRole.USER);
        CommentSaveRequest request = new CommentSaveRequest(null);

        // when & then
        mockMvc.perform(post("/todos/1/comments")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(request)))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.message").value("댓글은 빈칸이면 안됩니다"));
    }
}
