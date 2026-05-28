package com.example.couplebackend.entity;

import java.time.LocalDate;

public record CheckinRecord(Long id, Long spaceId, Long userId, LocalDate checkinDate, int continuousDays, int rewardPoint, Long rewardDressId) {
}
