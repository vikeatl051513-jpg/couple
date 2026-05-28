package com.example.couplebackend.entity;

public record User(Long id, String openid, String nickname, String title, String avatarUrl, int points, int contribution, String status) {
}
