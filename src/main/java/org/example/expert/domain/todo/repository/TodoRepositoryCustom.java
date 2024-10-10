package org.example.expert.domain.todo.repository;

import org.example.expert.domain.todo.entity.Todo;
import org.example.expert.domain.todo.entity.TodoSearchResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;

public interface TodoRepositoryCustom {
    Page<Todo> findTodosByConditions(String weather, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);
    Page<TodoSearchResult> searchTodos(String keyword, String nickname, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);
}
