package org.example.expert.domain.todo.service;

import org.example.expert.domain.todo.dto.response.TodoResponse;
import org.example.expert.domain.todo.entity.Todo;
import org.example.expert.domain.todo.repository.TodoRepository;
import org.example.expert.common.enums.UserRole;
import org.example.expert.domain.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.example.expert.domain.user.entity.User;


import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
public class TodoServiceTest {

    @Autowired
    private TodoRepository todoRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TodoService todoService;

    @Test
    public void 할일_페이지를_가져오는지_확인_쿼리도_확인() {
        // given
        User user = new User("user1@example.com", "password", UserRole.USER);
        User saveUser=userRepository.save(user);

        for (int i = 0; i < 10; i++) {
            todoRepository.save(new Todo("Title" + i, "Contents" + i, "Sunny", saveUser));
        }

        // when
        Page<TodoResponse> result = todoService.getTodos(1, 10);

        // then
        assertEquals(10, result.getContent().size());
        assertEquals("Title0", result.getContent().get(9).getTitle());
    }

    @Test
    public void 할일을_가져오는지_확인_쿼리도_확인() {
        // given
        User user = new User("user1@example.com", "password", UserRole.USER);
        User saveUser=userRepository.save(user);

        todoRepository.save(new Todo("Title", "Contents", "Sunny", saveUser));
        // when
        TodoResponse result = todoService.getTodo(1);
        // then
        assertEquals("Contents", result.getContents());
        assertEquals("Title", result.getTitle());
    }
}

