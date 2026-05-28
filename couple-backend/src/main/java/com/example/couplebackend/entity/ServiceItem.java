package com.example.couplebackend.entity;

public record ServiceItem(Long id, Long spaceId, Long categoryId, String name, String description, String imageUrl, String tag, int pointCost, int dailyLimit, int cooldownMinutes, boolean requireRemark, boolean requireAppointTime, int sortOrder, String status, int monthlySales, Long defaultAssigneeId) {
}
