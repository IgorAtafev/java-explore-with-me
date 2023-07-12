package ru.yandex.practicum.ewm.mapper;

import lombok.experimental.UtilityClass;
import ru.yandex.practicum.ewm.dto.CommentFullDto;
import ru.yandex.practicum.ewm.dto.CommentShortDto;
import ru.yandex.practicum.ewm.model.Comment;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public class CommentMapper {

    public CommentFullDto toFullDto(Comment comment) {
        CommentFullDto commentDto = new CommentFullDto();

        commentDto.setId(comment.getId());
        commentDto.setText(comment.getText());
        commentDto.setEvent(EventMapper.toShortDto(comment.getEvent()));
        commentDto.setAuthor(UserMapper.toShortDto(comment.getAuthor()));
        commentDto.setCreated(comment.getCreated());

        return commentDto;
    }

    public List<CommentFullDto> toFullDtos(Collection<Comment> comments) {
        return comments.stream()
                .map(CommentMapper::toFullDto)
                .collect(Collectors.toList());
    }

    public CommentShortDto toShortDto(Comment comment) {
        CommentShortDto commentDto = new CommentShortDto();

        commentDto.setId(comment.getId());
        commentDto.setText(comment.getText());
        commentDto.setAuthorName(comment.getAuthor().getName());
        commentDto.setCreated(comment.getCreated());

        return commentDto;
    }

    public List<CommentShortDto> toShortDtos(Collection<Comment> comments) {
        return comments.stream()
                .map(CommentMapper::toShortDto)
                .collect(Collectors.toList());
    }
}
