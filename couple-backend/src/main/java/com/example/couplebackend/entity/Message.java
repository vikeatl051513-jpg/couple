package com.example.couplebackend.entity;

import java.time.LocalDateTime;

public record Message(Long id, Long spaceId, Long receiverId, Long senderId, String type, String title, String content, Long bizId, boolean readStatus, LocalDateTime createdAt) {
}
