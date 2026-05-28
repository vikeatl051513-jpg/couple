package com.example.couplebackend.entity;

import java.time.LocalDateTime;

public record AuditRecord(Long id, String targetType, Long targetId, String status, String remark, LocalDateTime createdAt) {
}
