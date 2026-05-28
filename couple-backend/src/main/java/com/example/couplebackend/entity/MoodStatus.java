package com.example.couplebackend.entity;

import java.time.LocalDateTime;

public record MoodStatus(Long id, Long spaceId, Long userId, String moodKey, String moodLabel, String note, LocalDateTime updatedAt) {
}
