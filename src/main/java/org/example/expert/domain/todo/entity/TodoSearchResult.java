package org.example.expert.domain.todo.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class TodoSearchResult {
    private final String title;
    private final Long managerCount;
    private final Long commentCount;
}
