package com.example.couplebackend.entity;

public record SpaceDress(Long id, Long spaceId, Long dressItemId, Long obtainedBy, String obtainSource, boolean active) {
}
