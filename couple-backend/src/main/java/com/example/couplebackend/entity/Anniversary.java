package com.example.couplebackend.entity;

import java.time.LocalDate;

public record Anniversary(Long id, Long spaceId, String name, LocalDate date, boolean important) {
}
