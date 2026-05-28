package com.example.couplebackend.entity;

public record SpaceMember(Long id, Long spaceId, Long userId, String role, String displayName, String status) {
}
