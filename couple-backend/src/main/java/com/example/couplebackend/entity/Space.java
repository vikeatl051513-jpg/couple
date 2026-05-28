package com.example.couplebackend.entity;

public record Space(Long id, String name, String type, Long ownerUserId, String announcement, String coverUrl, Long currentDressId, String status, int level) {
}
