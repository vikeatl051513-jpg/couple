package com.example.couplebackend.entity;

public record UiOption(
        Long id,
        String groupKey,
        String optionKey,
        String label,
        String icon,
        String description,
        int sortOrder,
        String status
) {
}
