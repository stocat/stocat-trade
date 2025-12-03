package com.stocat.authapi.service;

import com.stocat.authapi.exception.AuthErrorCode;
import com.stocat.common.domain.member.domain.Member;
import com.stocat.common.domain.member.repository.MemberRepository;
import com.stocat.common.exception.ApiException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MemberCommandServiceTest {

    @Mock
    MemberRepository memberRepository;
    @Mock
    PasswordEncoder passwordEncoder;

    MemberCommandService service;

    @BeforeEach
    void setUp() {
        service = new MemberCommandService(memberRepository, passwordEncoder);
    }

    @Test
    void 로컬회원_생성시_중복이메일이면_예외() {
        when(memberRepository.existsByEmail("cat@stocat.com")).thenReturn(true);

        assertThatThrownBy(() -> service.createLocalMember("고냥이", "cat@stocat.com", "plain"))
                .isInstanceOf(ApiException.class)
                .hasFieldOrPropertyWithValue("errorCode", AuthErrorCode.EMAIL_ALREADY_EXISTS);
        verify(memberRepository, never()).save(any());
    }

    @Test
    void 로컬회원_생성시_중복닉네임이면_예외() {
        when(memberRepository.existsByEmail("cat@stocat.com")).thenReturn(false);
        when(memberRepository.existsByNickname("고냥이")).thenReturn(true);

        assertThatThrownBy(() -> service.createLocalMember("고냥이", "cat@stocat.com", "plain"))
                .isInstanceOf(ApiException.class)
                .hasFieldOrPropertyWithValue("errorCode", AuthErrorCode.NICKNAME_ALREADY_EXISTS);
        verify(memberRepository, never()).save(any());
    }

    @Test
    void 로컬회원_생성시_비밀번호를_인코딩하여_저장() {
        when(memberRepository.existsByEmail(anyString())).thenReturn(false);
        when(memberRepository.existsByNickname(anyString())).thenReturn(false);
        when(passwordEncoder.encode("plain")).thenReturn("encoded");
        // echo-back saved entity
        when(memberRepository.save(any(Member.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Member saved = service.createLocalMember("고냥이", "cat@stocat.com", "plain");

        assertThat(saved.getPassword()).isEqualTo("encoded");
        verify(passwordEncoder).encode("plain");
        verify(memberRepository).save(any(Member.class));
    }
}
