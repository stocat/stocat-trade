package com.stocat.authapi.service;

import com.stocat.authapi.exception.AuthErrorCode;
import com.stocat.authapi.service.dto.MemberDto;
import com.stocat.common.domain.member.domain.AuthProvider;
import com.stocat.common.domain.member.domain.Member;
import com.stocat.common.domain.member.domain.MemberRole;
import com.stocat.common.domain.member.domain.MemberStatus;
import com.stocat.common.domain.member.repository.MemberRepository;
import com.stocat.common.exception.ApiException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MemberQueryServiceTest {

    @Mock
    private MemberRepository memberRepository;

    private MemberQueryService queryService;

    @BeforeEach
    void setUp() {
        queryService = new MemberQueryService(memberRepository);
    }

    @Test
    void 이메일로_조회하면_DTO로_변환한다() {
        Member member = Member.builder()
                .id(1L)
                .nickname("고냥이")
                .email("cat@stocat.com")
                .password("encoded")
                .provider(AuthProvider.LOCAL)
                .providerId("pid")
                .status(MemberStatus.ACTIVE)
                .role(MemberRole.USER)
                .build();
        when(memberRepository.findByEmail("cat@stocat.com")).thenReturn(Optional.of(member));

        MemberDto dto = queryService.getMemberByEmail("cat@stocat.com");

        assertThat(dto.id()).isEqualTo(1L);
        assertThat(dto.email()).isEqualTo("cat@stocat.com");
        assertThat(dto.password()).isEqualTo("encoded");
    }

    @Test
    void 존재하지_않는_회원이면_예외를_던진다() {
        when(memberRepository.findByEmail("cat@stocat.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> queryService.getMemberByEmail("cat@stocat.com"))
                .isInstanceOf(ApiException.class)
                .hasFieldOrPropertyWithValue("errorCode", AuthErrorCode.MEMBER_NOT_FOUND);
    }
}
