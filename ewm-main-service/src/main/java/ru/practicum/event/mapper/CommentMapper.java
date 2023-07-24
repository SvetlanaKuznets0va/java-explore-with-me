package ru.practicum.event.mapper;


import ru.practicum.event.dto.CommentDto;
import ru.practicum.event.model.Comment;

import java.time.LocalDateTime;

public class CommentMapper {
    public static CommentDto toCommentDto(Comment comment, String authorName) {
        return new CommentDto(
                comment.getId(),
                comment.getText(),
                authorName,
                comment.getCreated()
        );
    }

    public static Comment toComment(int eventId, int userId, CommentDto text) {
        return new Comment(
                0,
                text.getText(),
                eventId,
                userId,
                LocalDateTime.now()
        );
    }
}
