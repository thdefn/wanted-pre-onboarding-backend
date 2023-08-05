package com.example.wanted.security;

import com.example.wanted.domain.model.RefreshToken;
import com.example.wanted.domain.repository.RefreshTokenRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class TokenProviderTest {
    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @InjectMocks
    private TokenProvider tokenProvider;

    @BeforeEach
    public void setUp(){
        ReflectionTestUtils.setField(tokenProvider, "secretKey", "abcde");
    }

    @Test
    void 토큰_발급_성공() {
        //given
        given(refreshTokenRepository.findById(anyString()))
                .willReturn(Optional.empty());
        //when
        ArgumentCaptor<RefreshToken> captor = ArgumentCaptor.forClass(RefreshToken.class);
        tokenProvider.generate("abcde@gmail.com");
        //then
        verify(refreshTokenRepository, times(1))
                .save(captor.capture());
        assertEquals("abcde@gmail.com", captor.getValue().getEmail());
    }

    @Test
    void 토큰_발급_성공_저장된_리프레쉬_토큰_존재() {
        //given
        RefreshToken refreshToken = mock(RefreshToken.class);
        given(refreshTokenRepository.findById(anyString()))
                .willReturn(Optional.of(refreshToken));
        //when
        tokenProvider.generate("abcde@gmail.com");
        //then
        verify(refreshToken, times(1)).setRefreshToken(anyString());
    }

}