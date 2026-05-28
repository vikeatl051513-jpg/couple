package com.example.couplebackend.entity;

import java.time.LocalDateTime;

public record TodoItem(Long id, Long spaceId, String title, String description, Long creatorId, Long assigneeId, int priority, LocalDateTime dueTime, String status) {
}
