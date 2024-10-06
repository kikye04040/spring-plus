package org.example.expert.domain.todo.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.example.expert.domain.comment.entity.QComment;
import org.example.expert.domain.manager.entity.QManager;
import org.example.expert.domain.todo.entity.QTodo;
import org.example.expert.domain.todo.entity.Todo;
import org.example.expert.domain.todo.entity.TodoSearchResult;
import org.example.expert.domain.user.entity.QUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
public class TodoRepositoryImpl implements TodoRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Todo> findTodosByConditions(String weather, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {
        QTodo todo = QTodo.todo;
        QUser user = QUser.user;

        JPAQuery<Todo> query = queryFactory
                .selectFrom(todo)
                .leftJoin(todo.user, user).fetchJoin()
                .where(
                        weather != null ? todo.weather.eq(weather) : null,
                        startDate != null ? todo.modifiedAt.goe(startDate) : null,
                        endDate != null ? todo.modifiedAt.loe(endDate) : null
                )
                .orderBy(todo.modifiedAt.desc());

        long total = query.fetchCount();
        List<Todo> todos = query
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        return new PageImpl<>(todos, pageable, total);
    }

    @Override
    public Page<TodoSearchResult> searchTodos(String keyword, String nickname, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {
        QTodo todo = QTodo.todo;
        QManager manager = QManager.manager;
        QComment comment = QComment.comment;
        QUser user = QUser.user;

        List<TodoSearchResult> results = queryFactory
                .select(Projections.constructor(
                        TodoSearchResult.class,
                        todo.title,
                        manager.countDistinct().as("managerCount"),
                        comment.countDistinct().as("commentCount")
                ))
                .from(todo)
                .leftJoin(todo.managers, manager)
                .leftJoin(manager.user, user)
                .leftJoin(todo.comments, comment)
                .where(
                        titleContains(keyword),
                        nicknameContains(nickname),
                        dateBetween(startDate, endDate)
                )
                .groupBy(todo.id)
                .orderBy(todo.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = queryFactory
                .select(todo.count())
                .from(todo)
                .where(
                        titleContains(keyword),
                        nicknameContains(nickname),
                        dateBetween(startDate, endDate)
                )
                .fetchOne();

        total = (total != null) ? total : 0L;

        return new PageImpl<>(results, pageable, total);
    }

    // 제목 검색 조건
    private BooleanExpression titleContains(String keyword) {
        return StringUtils.hasText(keyword) ? QTodo.todo.title.contains(keyword) : null;
    }

    // 닉네임 검색 조건
    private BooleanExpression nicknameContains(String nickname) {
        return StringUtils.hasText(nickname) ? QUser.user.nickname.contains(nickname) : null;
    }

    // 날짜 범위 검색 조건
    private BooleanExpression dateBetween(LocalDateTime startDate, LocalDateTime endDate) {
        if (startDate != null && endDate != null) {
            return QTodo.todo.createdAt.between(startDate, endDate);
        } else if (startDate != null) {
            return QTodo.todo.createdAt.goe(startDate); // startDate 만 있으면 그 이후로 검색
        } else if (endDate != null) {
            return QTodo.todo.createdAt.loe(endDate); // endDate 만 있으면 그 이전으로 검색
        } else {
            return null; // 둘 다 없으면 아무 조건도 추가하지 않음
        }
    }
}
