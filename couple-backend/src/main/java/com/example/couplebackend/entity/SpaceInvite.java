package com.example.couplebackend.entity;

import java.time.LocalDateTime;

public record SpaceInvite(Long id, Long spaceId, String inviteCode, Long createdBy, LocalDateTime expireAt,
                          int maxUseCount, int usedCount, String status) {
}
