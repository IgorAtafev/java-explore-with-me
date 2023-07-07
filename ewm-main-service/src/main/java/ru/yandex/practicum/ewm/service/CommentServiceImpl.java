package ru.yandex.practicum.ewm.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.ewm.dto.CommentForRequestDto;
import ru.yandex.practicum.ewm.dto.CommentFullDto;
import ru.yandex.practicum.ewm.dto.CommentShortDto;
import ru.yandex.practicum.ewm.mapper.CommentMapper;
import ru.yandex.practicum.ewm.model.Comment;
import ru.yandex.practicum.ewm.model.Event;
import ru.yandex.practicum.ewm.model.ParticipationRequestStatus;
import ru.yandex.practicum.ewm.model.User;
import ru.yandex.practicum.ewm.repository.CommentRepository;
import ru.yandex.practicum.ewm.repository.EventRepository;
import ru.yandex.practicum.ewm.repository.ParticipationRequestRepository;
import ru.yandex.practicum.ewm.repository.UserRepository;
import ru.yandex.practicum.ewm.specification.CommentSpecification;
import ru.yandex.practicum.ewm.util.CommentRequestParam;
import ru.yandex.practicum.ewm.validator.ConflictException;
import ru.yandex.practicum.ewm.validator.NotFoundException;
import ru.yandex.practicum.ewm.validator.ValidationException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final ParticipationRequestRepository requestRepository;

    @Override
    public CommentFullDto createComment(Long userId, CommentForRequestDto commentDto) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException(String.format("Author with id %d does not exist", userId)));

        Event event = eventRepository.findById(commentDto.getEventId()).orElseThrow(
                () -> new NotFoundException(String.format(
                        "Event with id %d does not exist", commentDto.getEventId())));

        if (!requestRepository.existsByRequesterIdAndEventIdAndStatus(
                userId, commentDto.getEventId(), ParticipationRequestStatus.CONFIRMED
        )) {
            throw new ConflictException(String.format("Request with requester id %d and event id %d does not exist",
                    userId, commentDto.getEventId()));
        }

        if (commentRepository.existsByAuthorIdAndEventId(userId, commentDto.getEventId())) {
            throw new ConflictException(String.format("Comment with author id %d and event id %d exists",
                    userId, commentDto.getEventId()));
        }

        Comment comment = new Comment();

        comment.setText(commentDto.getText());
        comment.setEvent(event);
        comment.setAuthor(user);
        comment.setCreated(LocalDateTime.now());

        return CommentMapper.toFullDto(commentRepository.save(comment));
    }

    @Override
    public CommentFullDto updateUserComment(Long userId, Long id, CommentForRequestDto commentDto) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException(String.format("Author with id %d does not exist", userId));
        }

        Comment comment = commentRepository.findByIdAndAuthorId(id, userId).orElseThrow(
                () -> new NotFoundException(String.format(
                        "Comment with id %d and author id %d does not exist", id, userId)));

        comment.setText(commentDto.getText());

        return CommentMapper.toFullDto(commentRepository.save(comment));
    }

    @Override
    public void removeUserComment(Long userId, Long id) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException(String.format("Author with id %d does not exist", userId));
        }

        if (!commentRepository.existsByIdAndAuthorId(id, userId)) {
            throw new NotFoundException(String.format("Comment with id %d and author id %d does not exist", id));
        }

        commentRepository.deleteById(id);
    }

    @Override
    public void removeCommentByAdmin(Long id) {
        if (!commentRepository.existsById(id)) {
            throw new NotFoundException(String.format("Comment with id %d does not exist", id));
        }

        commentRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    @Override
    public List<CommentFullDto> getUserComments(Long userId, Pageable page) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException(String.format("Author with id %d does not exist", userId));
        }

        return CommentMapper.toFullDtos(commentRepository.findByAuthorId(userId, page));
    }

    @Transactional(readOnly = true)
    @Override
    public CommentFullDto getUserCommentById(Long userId, Long id) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException(String.format("Author with id %d does not exist", userId));
        }

        Comment comment = commentRepository.findByIdAndAuthorId(id, userId).orElseThrow(
                () -> new NotFoundException(String.format("Comment with id %d and author id %d does not exist",
                        id, userId)));

        return CommentMapper.toFullDto(comment);
    }

    @Transactional(readOnly = true)
    @Override
    public List<CommentFullDto> getCommentsByAdmin(CommentRequestParam requestParam, Pageable page) {
        if (requestParam.getRangeStart() != null && requestParam.getRangeEnd() != null
                && requestParam.getRangeStart().isAfter(requestParam.getRangeEnd())
        ) {
            throw new ValidationException(String.format("The start of the range must be before the end of the range"));
        }

        List<Specification> conditions = new ArrayList<>();

        if (requestParam.getEvents() != null) {
            conditions.add(CommentSpecification.findByEventIdIn(requestParam.getEvents()));
        }

        if (requestParam.getUsers() != null) {
            conditions.add(CommentSpecification.findByAuthorIdIn(requestParam.getUsers()));
        }

        if (requestParam.getRangeStart() != null) {
            conditions.add(CommentSpecification.findByRangeStartGreaterThanEqual(requestParam.getRangeStart()));
        }
        if (requestParam.getRangeEnd() != null) {
            conditions.add(CommentSpecification.findByRangeEndLessThanEqual(requestParam.getRangeEnd()));
        }

        List<Comment> comments = getCommentsByCondition(conditions, page);

        return CommentMapper.toFullDtos(comments);
    }

    @Transactional(readOnly = true)
    @Override
    public List<CommentShortDto> getCommentsToEvent(Long eventId) {
        if (!eventRepository.existsById(eventId)) {
            throw new NotFoundException(String.format("Event with id %d does not exist", eventId));
        }

        return CommentMapper.toShortDtos(commentRepository.findByEventId(eventId));
    }

    @Transactional(readOnly = true)
    @Override
    public CommentShortDto getCommentToEventById(Long eventId, Long id) {
        if (!eventRepository.existsById(eventId)) {
            throw new NotFoundException(String.format("Event with id %d does not exist", eventId));
        }

        Comment comment = commentRepository.findByIdAndEventId(id, eventId).orElseThrow(
                () -> new NotFoundException(String.format("Comment with id %d and Event id %d does not exist",
                        id, eventId)));

        return CommentMapper.toShortDto(comment);
    }

    private List<Comment> getCommentsByCondition(List<Specification> conditions, Pageable page) {
        List<Comment> comments;

        if (!conditions.isEmpty()) {
            conditions.set(0, Specification.where(conditions.get(0)));
            Specification finalCondition = conditions.stream()
                    .reduce(Specification::and)
                    .get();

            comments = commentRepository.findAll(finalCondition, page).toList();
        } else {
            comments = commentRepository.findAll(page).toList();
        }

        return comments;
    }
}
