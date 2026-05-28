package com.example.couplebackend.entity;

public record WishItem(Long id, Long spaceId, Long creatorId, Long claimedBy, String title, String description, String type, String status) {
}
