package com.example.couplebackend.entity;

import java.time.LocalDate;

public record Activity(Long id, String name, String type, String description, LocalDate startDate, LocalDate endDate, int rewardPoint, Long rewardDressId, String status) {
}
