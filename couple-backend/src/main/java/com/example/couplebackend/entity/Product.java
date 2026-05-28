package com.example.couplebackend.entity;

public record Product(Long id, String name, String type, Long targetId, long amountCent, String status) {
}
