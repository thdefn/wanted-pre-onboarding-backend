package com.example.wanted.controller;

import com.example.wanted.dto.SignInForm;
import com.example.wanted.dto.SignUpForm;
import com.example.wanted.dto.TokenDto;
import com.example.wanted.dto.TokenRefreshForm;
import com.example.wanted.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/sign-up")
    private ResponseEntity<Void> register(@Valid @RequestBody SignUpForm form) {
        authService.register(form);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/sign-in")
    private ResponseEntity<TokenDto> login(@Valid @RequestBody SignInForm form) {
        return ResponseEntity.ok(authService.login(form));
    }

    @PostMapping("/reissue")
    private ResponseEntity<TokenDto> reissue(@Valid @RequestBody TokenRefreshForm form) {
        return ResponseEntity.ok(authService.reissue(form.getRefreshToken()));
    }
}
