package com.example.wanted.service;

import com.example.wanted.domain.constants.MemberType;
import com.example.wanted.domain.model.Member;
import com.example.wanted.domain.repository.MemberRepository;
import com.example.wanted.dto.SignUpForm;
import com.example.wanted.exception.MemberException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import static com.example.wanted.exception.ErrorCode.MEMBER_ALREADY_REGISTERED;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final PasswordEncoder passwordEncoder;

    private final MemberRepository memberRepository;

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
}
