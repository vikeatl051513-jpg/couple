package com.example.couplebackend.entity;

public record ServiceCategory(Long id, Long spaceId, String name, String description, String iconUrl, int sortOrder, boolean visible) {
}
