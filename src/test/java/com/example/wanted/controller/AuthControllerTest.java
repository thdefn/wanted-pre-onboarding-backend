package com.example.wanted.controller;

import com.example.wanted.dto.SignInForm;
import com.example.wanted.dto.SignUpForm;
import com.example.wanted.dto.TokenDto;
import com.example.wanted.dto.TokenRefreshForm;
import com.example.wanted.service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {
    @MockBean
    private AuthService authService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void 회원가입_성공() throws Exception {
        //given
        SignUpForm form = SignUpForm.builder()
                .email("thdefn@gmail.com")
                .password("12345678")
                .build();
        //when
        //then
        mockMvc.perform(post("/auth/sign-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(form))
                )
                .andDo(print())
                .andExpect(status().isOk());

    }

    @Test
    void 회원가입_실패_유효한_이메일이_아님() throws Exception {
        //given
        SignUpForm form = SignUpForm.builder()
                .email("thdefngmail.com")
                .password("12345678")
                .build();
        //when
        //then
        mockMvc.perform(post("/auth/sign-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(form))
                )
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void 회원가입_실패_8자_미만의_비밀번호() throws Exception {
        //given
        SignUpForm form = SignUpForm.builder()
                .email("thdefn@gmail.com")
                .password("1234567")
                .build();
        //when
        //then
        mockMvc.perform(post("/auth/sign-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(form))
                )
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void 로그인_성공() throws Exception {
        //given
        SignInForm form = SignInForm.builder()
                .email("thdefn@gmail.com")
                .password("12345678")
                .build();
        //when
        given(authService.login(any()))
                .willReturn(TokenDto.builder()
                        .accessToken("Bearer accessToken")
                        .refreshToken("Bearer refreshToken")
                        .build());
        //then
        mockMvc.perform(post("/auth/sign-in")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(form))
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("Bearer accessToken"))
                .andExpect(jsonPath("$.refreshToken").value("Bearer refreshToken"));
    }

    @Test
    void 로그인_실패_유효한_이메일이_아님() throws Exception {
        //given
        SignInForm form = SignInForm.builder()
                .email("thdefngmail.com")
                .password("12345678")
                .build();
        //when
        //then
        mockMvc.perform(post("/auth/sign-in")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(form))
                )
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void 로그인_실패_8자_미만의_비밀번호() throws Exception {
        //given
        SignInForm form = SignInForm.builder()
                .email("thdefn@gmail.com")
                .password("1234567")
                .build();
        //when
        //then
        mockMvc.perform(post("/auth/sign-in")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(form))
                )
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void 토큰_재발급_성공() throws Exception {
        //given
        TokenRefreshForm form = TokenRefreshForm.builder()
                .refreshToken("Bearer refreshToken")
                .build();
        //when
        given(authService.reissue(any()))
                .willReturn(TokenDto.builder()
                        .accessToken("Bearer accessToken")
                        .refreshToken("Bearer refreshToken")
                        .build());
        //then
        mockMvc.perform(post("/auth/reissue")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(form))
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("Bearer accessToken"))
                .andExpect(jsonPath("$.refreshToken").value("Bearer refreshToken"));
    }

    @Test
    void 토큰_재발급_실패() throws Exception {
        //given
        TokenRefreshForm form = TokenRefreshForm.builder()
                .refreshToken("Bearer")
                .build();
        //when
        //then
        mockMvc.perform(post("/auth/reissue")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(form))
                )
                .andDo(print())
                .andExpect(status().isBadRequest());
    }
}