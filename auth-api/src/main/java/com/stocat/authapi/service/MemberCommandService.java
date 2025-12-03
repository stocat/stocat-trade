package com.stocat.authapi.service;

import com.stocat.authapi.exception.AuthErrorCode;
import com.stocat.common.domain.member.domain.AuthProvider;
import com.stocat.common.domain.member.domain.Member;
import com.stocat.common.domain.member.domain.MemberRole;
import com.stocat.common.domain.member.domain.MemberStatus;
import com.stocat.common.domain.member.repository.MemberRepository;
import com.stocat.common.exception.ApiException;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberCommandService {

    private final MemberRepository memberRepository;
    private final org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    public Member createLocalMember(@NonNull String nickname,
                                    @NonNull String email,
                                    @NonNull String rawPassword) {
        if (memberRepository.existsByEmail(email)) {
            throw new ApiException(AuthErrorCode.EMAIL_ALREADY_EXISTS);
        }
        if (memberRepository.existsByNickname(nickname)) {
            throw new ApiException(AuthErrorCode.NICKNAME_ALREADY_EXISTS);
        }
        String encoded = passwordEncoder.encode(rawPassword);
        Member member = Member.create(
                nickname,
                email,
                encoded,
                AuthProvider.LOCAL,
                "",
                MemberStatus.ACTIVE,
                MemberRole.USER
        );
        return memberRepository.save(member);
    }

    public void markLoginAt(@NonNull Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ApiException(AuthErrorCode.MEMBER_NOT_FOUND));
        member.markLoggedInNow();
    }

    // Update: status change
    public void changeStatus(@NonNull Long memberId, @NonNull MemberStatus status) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ApiException(AuthErrorCode.MEMBER_NOT_FOUND));
        member.changeStatus(status);
        memberRepository.save(member);
    }

    // Delete
    public void delete(@NonNull Long memberId) {
        memberRepository.deleteById(memberId);
    }
}
