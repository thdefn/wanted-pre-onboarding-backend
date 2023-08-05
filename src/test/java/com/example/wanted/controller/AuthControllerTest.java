package com.example.wanted.controller;

import com.example.wanted.dto.SignUpForm;
import com.example.wanted.service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
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
    @DisplayName("회원가입 성공")
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
    @DisplayName("회원가입 실패 - 유효한 이메일이 아님")
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
    @DisplayName("회원가입 실패 - 8자리 이상의 비밀번호가 아님")
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
}