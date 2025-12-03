package com.stocat.common.domain.member.domain;

import com.stocat.common.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@EqualsAndHashCode(of = "id", callSuper = false)
@Table(name = "members")
@Entity
public class Member extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String nickname;

    @Column(nullable = false, length = 180)
    private String email;

    @Column(name = "password", length = 100)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AuthProvider provider;

    @Column(name = "provider_id", length = 100)
    private String providerId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private MemberStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private MemberRole role;

    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt;

    public static Member create(String nickname, String email, String password, AuthProvider provider, String providerId, MemberStatus status, MemberRole role) {
        return Member.builder()
                .nickname(nickname)
                .email(email)
                .password(password)
                .provider(provider)
                .providerId(providerId)
                .status(status)
                .role(role)
                .build();
    }

    public void markLoggedInNow() {
        this.lastLoginAt = LocalDateTime.now();
    }

    public void changeStatus(MemberStatus status) {
        this.status = status;
    }
}

