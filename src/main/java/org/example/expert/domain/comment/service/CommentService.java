package org.example.expert.domain.comment.service;

import lombok.RequiredArgsConstructor;
import org.example.expert.domain.comment.dto.request.CommentSaveRequest;
import org.example.expert.domain.comment.dto.request.CommentUpdateRequest;
import org.example.expert.domain.comment.dto.response.CommentResponse;
import org.example.expert.domain.comment.dto.response.CommentSaveResponse;
import org.example.expert.domain.comment.entity.Comment;
import org.example.expert.domain.comment.repository.CommentRepository;
import org.example.expert.common.auth.dto.AuthUser;
import org.example.expert.common.exception.custom.InvalidRequestException;
import org.example.expert.domain.todo.entity.Todo;
import org.example.expert.domain.todo.repository.TodoRepository;
import org.example.expert.domain.user.dto.response.UserResponse;
import org.example.expert.domain.user.entity.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final TodoRepository todoRepository;
    private final CommentRepository commentRepository;

    @Transactional
    public CommentSaveResponse saveComment(AuthUser authUser, long todoId, CommentSaveRequest commentSaveRequest) {
        User user = User.fromAuthUser(authUser);
        Todo todo = todoRepository.findById(todoId).orElseThrow(() ->
                new InvalidRequestException("Todo not found"));

        Comment newComment = new Comment(
                commentSaveRequest.getContents(),
                user,
                todo
        );

        Comment savedComment = commentRepository.save(newComment);

        return new CommentSaveResponse(
                savedComment.getId(),
                savedComment.getContents(),
                new UserResponse(user.getId(), user.getEmail())
        );
    }

    @Transactional(readOnly = true)
    public List<CommentResponse> getComments(long todoId) {
        List<Comment> commentList = commentRepository.findByTodoIdWithUser(todoId);

        List<CommentResponse> dtoList = new ArrayList<>();
        for (Comment comment : commentList) {
            User user = comment.getUser();
            CommentResponse dto = new CommentResponse(
                    comment.getId(),
                    comment.getContents(),
                    new UserResponse(user.getId(), user.getEmail())
            );
            dtoList.add(dto);
        }
        return dtoList;
    }
    @Transactional
    public void updateComments(AuthUser authUser, long commentId, CommentUpdateRequest commentUpdateRequest){
        User user = User.fromAuthUser(authUser);

        Comment comment =commentRepository.findById(commentId).
                orElseThrow(()-> new InvalidRequestException("댓글을 찾을 수 없습니다."));

        if (!user.getId().equals(comment.getUser().getId())){
            throw new InvalidRequestException("댓글을 바꿀 권한이 없습니다");
        }

        comment.update(commentUpdateRequest.getContents());
    }

    @Transactional
    public void deleteComment(AuthUser authUser,long commentId) {
        User user = User.fromAuthUser(authUser);

        Comment comment =commentRepository.findById(commentId).
                orElseThrow(()-> new InvalidRequestException("댓글을 찾을 수 없습니다."));

        if (!user.getId().equals(comment.getUser().getId())){
            throw new InvalidRequestException("댓글을 바꿀 권한이 없습니다");
        }

        commentRepository.deleteById(commentId);
    }
}
