package com.example.wanted.service;

import com.example.wanted.domain.model.Member;
import com.example.wanted.domain.repository.MemberRepository;
import com.example.wanted.dto.SignUpForm;
import com.example.wanted.exception.MemberException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static com.example.wanted.exception.ErrorCode.MEMBER_ALREADY_REGISTERED;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {
    @Mock
    private MemberRepository memberRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    Member member = Member.builder()
            .email("abcde@gmail.com")
            .password("12345678")
            .build();

    @Test
    @DisplayName("회원가입 성공")
    void 회원가입_성공() {
        //given
        SignUpForm form = SignUpForm.builder()
                .email("abcde@gmail.com")
                .password("12345678")
                .build();
        given(memberRepository.existsByEmail(anyString())).willReturn(false);
        given(passwordEncoder.encode(anyString())).willReturn("encodedpassword");
        //when
        ArgumentCaptor<Member> captor = ArgumentCaptor.forClass(Member.class);
        authService.register(form);
        //then
        verify(memberRepository, times(1)).save(captor.capture());
        assertNotEquals("12345678", captor.getValue().getPassword());
    }

    @Test
    @DisplayName("회원가입 실패 - 이미 존재하는 유저")
    void 회원가입_실패_이미_존재하는_유저() {
        //given
        SignUpForm form = SignUpForm.builder()
                .email("abcde@gmail.com")
                .password("12345678")
                .build();
        given(memberRepository.existsByEmail(anyString())).willReturn(true);
        //when
        MemberException exception = assertThrows(MemberException.class, () -> authService.register(form));
        //then
        assertEquals(exception.getErrorCode(), MEMBER_ALREADY_REGISTERED);
    }
}