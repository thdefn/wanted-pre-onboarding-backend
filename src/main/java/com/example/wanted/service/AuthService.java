package com.example.wanted.service;

import com.example.wanted.domain.constants.MemberType;
import com.example.wanted.domain.model.Member;
import com.example.wanted.domain.model.RefreshToken;
import com.example.wanted.domain.repository.MemberRepository;
import com.example.wanted.domain.repository.RefreshTokenRepository;
import com.example.wanted.dto.SignInForm;
import com.example.wanted.dto.SignUpForm;
import com.example.wanted.dto.TokenDto;
import com.example.wanted.exception.MemberException;
import com.example.wanted.security.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.example.wanted.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final PasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final TokenProvider tokenProvider;

    public void register(SignUpForm form) {
        if (memberRepository.existsByEmail(form.getEmail()))
            throw new MemberException(MEMBER_ALREADY_REGISTERED);

        memberRepository.save(
                Member.builder()
                        .email(form.getEmail())
                        .password(passwordEncoder.encode(form.getPassword()))
                        .type(MemberType.USER)
                        .build());
    }

    @Transactional
    public TokenDto login(SignInForm form) {
        Member member = memberRepository.findByEmail(form.getEmail())
                .orElseThrow(() -> new MemberException(MEMBER_NOT_FOUND));
        if (!passwordEncoder.matches(form.getPassword(), member.getPassword()))
            throw new MemberException(PASSWORD_UNMATCHED);

        return tokenProvider.generate(member.getEmail());
    }

    @Transactional
    public TokenDto reissue(String refreshToken) {
        RefreshToken token = refreshTokenRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new MemberException(TOKEN_EXPIRED));
        if (!tokenProvider.validate(token.getRefreshToken()))
            throw new MemberException(TOKEN_EXPIRED);

        return tokenProvider.generate(token.getEmail());
    }
}
