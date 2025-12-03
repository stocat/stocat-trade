package com.stocat.authapi.service;

import com.stocat.authapi.security.jwt.JwtClaimKeys;
import com.stocat.authapi.security.jwt.JwtProvider;
import com.stocat.authapi.controller.dto.AuthResponse;
import com.stocat.authapi.controller.dto.LoginRequest;
import com.stocat.authapi.controller.dto.SignupRequest;
import com.stocat.authapi.exception.AuthErrorCode;
import com.stocat.authapi.service.dto.MemberDto;
import com.stocat.common.exception.ApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final MemberCommandService commandService;
    private final MemberQueryService queryService;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    /**
     * Use case: signup
     *
     * @param request 회원가입 req
     */
    public void signup(SignupRequest request) {
        commandService.createLocalMember(request.nickname(), request.email(), request.password());
    }

    /**
     * Use case: login
     *
     * @param request 로그인 req
     * @return AuthResponse(유저 토큰)
     */
    @Transactional
    public AuthResponse login(LoginRequest request) {
        MemberDto member = queryService.getMemberByEmail(request.email());

        if (member.password() == null || !passwordEncoder.matches(request.password(), member.password())) {
            throw new ApiException(AuthErrorCode.INVALID_CREDENTIALS);
        }

        commandService.markLoginAt(member.id());

        String access = jwtProvider.createAccessToken(
                String.valueOf(member.id()),
                Map.of(
                        JwtClaimKeys.MEMBER_ID, member.id(),
                        JwtClaimKeys.EMAIL, member.email(),
                        JwtClaimKeys.ROLE, member.role().name()
                )
        );
        String refresh = jwtProvider.createRefreshToken(String.valueOf(member.id()));
        return new AuthResponse(access, refresh);
    }
}
