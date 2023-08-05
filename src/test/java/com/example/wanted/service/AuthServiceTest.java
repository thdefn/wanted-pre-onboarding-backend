package com.example.wanted.service;

import com.example.wanted.domain.model.Member;
import com.example.wanted.domain.model.RefreshToken;
import com.example.wanted.domain.repository.MemberRepository;
import com.example.wanted.domain.repository.RefreshTokenRepository;
import com.example.wanted.dto.SignInForm;
import com.example.wanted.dto.SignUpForm;
import com.example.wanted.dto.TokenDto;
import com.example.wanted.exception.MemberException;
import com.example.wanted.security.TokenProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static com.example.wanted.exception.ErrorCode.*;
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
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private TokenProvider tokenProvider;

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

    @Test
    void 로그인_성공() {
        //given
        SignInForm form = SignInForm.builder()
                .email("abcde@gmail.com")
                .password("12345678")
                .build();
        given(memberRepository.findByEmail(anyString()))
                .willReturn(Optional.of(Member.builder()
                        .email("abcde@gmail.com")
                        .password("encoded-password").build()));
        given(passwordEncoder.matches(anyString(), anyString())).willReturn(true);
        given(tokenProvider.generate(anyString()))
                .willReturn(TokenDto.builder()
                        .accessToken("Bearer accessToken")
                        .refreshToken("Bearer refreshToken")
                        .build());
        //when
        TokenDto dto = authService.login(form);
        //then
        assertEquals(dto.getAccessToken(), "Bearer accessToken");
        assertEquals(dto.getRefreshToken(), "Bearer refreshToken");
    }

    @Test
    void 로그인_실패_해당_이메일_없음() {
        //given
        SignInForm form = SignInForm.builder()
                .email("abcde@gmail.com")
                .password("12345678")
                .build();
        given(memberRepository.findByEmail(anyString())).willReturn(Optional.empty());
        //when
        MemberException exception = assertThrows(MemberException.class, () -> authService.login(form));
        //then
        assertEquals(exception.getErrorCode(), MEMBER_NOT_FOUND);
    }

    @Test
    void 로그인_실패_패스워드가_다름() {
        //given
        SignInForm form = SignInForm.builder()
                .email("abcde@gmail.com")
                .password("12345678")
                .build();
        given(memberRepository.findByEmail(anyString()))
                .willReturn(Optional.of(Member.builder()
                        .email("abcde@gmail.com")
                        .password("encoded-password").build()));
        given(passwordEncoder.matches(anyString(), anyString())).willReturn(false);
        //when
        MemberException exception = assertThrows(MemberException.class,
                () -> authService.login(form));
        //then
        assertEquals(exception.getErrorCode(), PASSWORD_UNMATCHED);
    }

    @Test
    void 토큰_재발급_성공() {
        //given
        String refreshToken = "Bearer refreshToken";
        given(refreshTokenRepository.findByRefreshToken(anyString()))
                .willReturn(Optional.of(RefreshToken.builder()
                        .email("abcde@gmail.com")
                        .refreshToken(refreshToken).build()));
        given(tokenProvider.validate(anyString())).willReturn(true);
        given(tokenProvider.generate(anyString()))
                .willReturn(TokenDto.builder()
                        .accessToken("Bearer accessToken")
                        .refreshToken("Bearer refreshToken")
                        .build());
        //when
        TokenDto dto = authService.reissue(refreshToken);
        //then
        assertEquals(dto.getAccessToken(), "Bearer accessToken");
        assertEquals(dto.getRefreshToken(), "Bearer refreshToken");
    }

    @Test
    void 토큰_재발급_실패_해당_토큰_없음() {
        //given
        String refreshToken = "Bearer refreshToken";
        given(refreshTokenRepository.findByRefreshToken(anyString()))
                .willReturn(Optional.empty());
        //when
        MemberException exception = assertThrows(MemberException.class,
                () -> authService.reissue(refreshToken));
        //then
        assertEquals(exception.getErrorCode(), TOKEN_EXPIRED);
    }

    @Test
    void 토큰_재발급_실패_만료된_토큰() {
        //given
        String refreshToken = "Bearer refreshToken";
        given(refreshTokenRepository.findByRefreshToken(anyString()))
                .willReturn(Optional.of(RefreshToken.builder()
                        .email("abcde@gmail.com")
                        .refreshToken(refreshToken).build()));
        given(tokenProvider.validate(anyString())).willReturn(false);
        //when
        MemberException exception = assertThrows(MemberException.class,
                () -> authService.reissue(refreshToken));
        //then
        assertEquals(exception.getErrorCode(), TOKEN_EXPIRED);
    }
}