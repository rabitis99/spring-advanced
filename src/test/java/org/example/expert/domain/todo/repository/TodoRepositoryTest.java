package org.example.expert.domain.todo.repository;

import org.example.expert.common.enums.UserRole;
import org.example.expert.domain.todo.entity.Todo;
import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class TodoRepositoryTest {

    @Autowired
    TodoRepository todoRepository;

    @Autowired
    UserRepository userRepository;

    @Test
    @DisplayName("Todo를 수정일자 기준으로 내림차순 조회")
    void findAllByOrderByModifiedAtDesc() {
        // given
        User user = new User("user4@example.com", "password", UserRole.USER);
        User saveUser = userRepository.save(user);

        for (int i = 0; i < 10; i++) {
            Todo todo = new Todo("Title" + i, "Contents" + i, "Sunny", saveUser);
            todoRepository.save(todo);
        }

        Pageable pageable = PageRequest.of(0, 10);

        // when
        Page<Todo> result = todoRepository.findAllByOrderByModifiedAtDesc(pageable);

        // then
        assertThat(result.getContent()).hasSize(10);
        assertThat(result.getContent().get(0).getTitle()).isEqualTo("Title9"); // 가장 최근 것이 먼저
    }

    @Test
    @DisplayName("ID로 Todo를 조회하면서 User도 함께 로딩")
    void findByIdWithUser() {
        // given
        User user = new User("user5@example.com", "password", UserRole.USER);
        user = userRepository.save(user);

        Todo todo = new Todo("Test Title", "Test Content", "Cloudy", user);
        todo = todoRepository.save(todo);

        // when
        Optional<Todo> found = todoRepository.findByIdWithUser(todo.getId());

        // then
        assertThat(found).isPresent();
        assertThat(found.get().getUser().getEmail()).isEqualTo("user5@example.com");
    }
}
