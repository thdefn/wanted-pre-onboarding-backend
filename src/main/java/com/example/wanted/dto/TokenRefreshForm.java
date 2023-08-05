package com.example.wanted.dto;

import jakarta.validation.constraints.Pattern;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TokenRefreshForm {
    @Pattern(regexp = "^(Bearer)\s[0-9|a-z|A-Z|.]*$", message = "적합한 토큰값이 아닙니다.")
    private String refreshToken;
}
