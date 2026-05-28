package com.example.couplebackend.entity;

import java.time.LocalDateTime;

public record ServiceOrder(Long id, String orderNo, Long spaceId, Long serviceItemId, Long requesterId, Long assigneeId, String status, String remark, LocalDateTime appointTime, LocalDateTime createdAt, LocalDateTime acceptedAt, LocalDateTime completedAt, LocalDateTime canceledAt) {
}
