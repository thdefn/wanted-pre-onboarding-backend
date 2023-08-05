package com.example.wanted.controller;

import com.example.wanted.dto.SignUpForm;
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
        log.error(form.getEmail());
        authService.register(form);
        return ResponseEntity.ok().build();
    }
}
