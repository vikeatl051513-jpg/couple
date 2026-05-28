package com.example.couplebackend.entity;

import java.time.LocalDateTime;

public record PaymentOrder(Long id, String payNo, Long userId, Long spaceId, String productType, Long productId, long amountCent, String status, LocalDateTime paidAt) {
}
