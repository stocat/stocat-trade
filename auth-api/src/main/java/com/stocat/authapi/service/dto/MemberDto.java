package com.stocat.authapi.service.dto;

import com.stocat.common.domain.member.domain.AuthProvider;
import com.stocat.common.domain.member.domain.Member;
import com.stocat.common.domain.member.domain.MemberRole;
import com.stocat.common.domain.member.domain.MemberStatus;

import java.time.LocalDateTime;

public record MemberDto(
        Long id,
        String nickname,
        String email,
        String password,
        AuthProvider provider,
        String providerId,
        MemberStatus status,
        MemberRole role,
        LocalDateTime lastLoginAt,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        LocalDateTime deletedAt
) {
    public static MemberDto from(Member m) {
        return new MemberDto(
                m.getId(),
                m.getNickname(),
                m.getEmail(),
                m.getPassword(),
                m.getProvider(),
                m.getProviderId(),
                m.getStatus(),
                m.getRole(),
                m.getLastLoginAt(),
                m.getCreatedAt(),
                m.getUpdatedAt(),
                m.getDeletedAt()
        );
    }
}
