package com.stocat.authapi.service;

import com.stocat.authapi.exception.AuthErrorCode;
import com.stocat.authapi.service.dto.MemberDto;
import com.stocat.common.domain.member.domain.Member;
import com.stocat.common.domain.member.repository.MemberRepository;
import com.stocat.common.exception.ApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberQueryService {

    private final MemberRepository memberRepository;

    public Optional<Member> getById(Long id) {
        return memberRepository.findById(id);
    }

    public Optional<Member> findByEmail(String email) {
        return memberRepository.findByEmail(email);
    }

    public boolean existsByEmail(String email) {
        return memberRepository.existsByEmail(email);
    }

    public boolean existsByNickname(String nickname) {
        return memberRepository.existsByNickname(nickname);
    }

    // Exception-throwing getters for simpler read use-cases
    public Member getByIdOrThrow(Long id) {
        return getById(id).orElseThrow(() -> new ApiException(AuthErrorCode.MEMBER_NOT_FOUND));
    }

    public Member getByEmailOrThrow(String email) {
        return findByEmail(email).orElseThrow(() -> new ApiException(AuthErrorCode.MEMBER_NOT_FOUND));
    }

    // View mappers to detach persistence concerns
    public MemberDto getMemberById(Long id) {
        Member member = getByIdOrThrow(id);
        return MemberDto.from(member);
    }

    public MemberDto getMemberByEmail(String email) {
        Member member = getByEmailOrThrow(email);
        return MemberDto.from(member);
    }
}
