package org.example.expert.domain.admin.controller;

import org.example.expert.common.enums.UserRole;
import org.example.expert.common.jwt.JwtUtil;
import org.example.expert.domain.comment.entity.Comment;
import org.example.expert.domain.todo.entity.Todo;
import org.example.expert.domain.user.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
class CommentAdminControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private JwtUtil jwtUtil;
    @Test
    void deleteComment() throws Exception {
        //given
        long commentId = 1L;
        User user=new User("email","password", UserRole.USER);
        Todo todo=new Todo("title","contents","weather",user);
        Comment comment =new Comment("contents",user,todo);
        ReflectionTestUtils.setField(comment, "id", 1L);

        String token = jwtUtil.createToken(1L, "test@example.com", UserRole.ADMIN);
        //when & then
        mockMvc.perform(delete("/admin/comments/{commentId}", commentId)
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

    }
}