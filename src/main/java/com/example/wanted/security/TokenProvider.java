package com.example.wanted.security;

import com.example.wanted.domain.model.RefreshToken;
import com.example.wanted.domain.repository.RefreshTokenRepository;
import com.example.wanted.dto.TokenDto;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Date;

@RequiredArgsConstructor
@Service
public class TokenProvider {
    @Value("${spring.jwt.secret}")
    private String secretKey;
    private static final long ACCESS_AVAILABLE_TIME = 1000 * 60 * 30;
    private static final long REFRESH_AVAILABLE_TIME = 1000 * 60 * 60 * 24 * 14;

    private final RefreshTokenRepository refreshTokenRepository;

    public TokenDto generate(String email) {
        Date now = new Date();
        String accessToken = Jwts.builder()
                .setSubject(email)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + ACCESS_AVAILABLE_TIME))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
        String refreshToken = Jwts.builder()
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + REFRESH_AVAILABLE_TIME))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();

        refreshTokenRepository.findById(email)
                .ifPresentOrElse(token -> token.setRefreshToken(refreshToken),
                        () -> refreshTokenRepository.save(RefreshToken.builder()
                                .email(email)
                                .refreshToken(refreshToken)
                                .build()));

        return TokenDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    public boolean validate(String token) {
        if (!StringUtils.hasText(token))
            return false;
        Claims claims = parseClaims(token);
        return claims.getExpiration().after(new Date());
    }

    public String getEmail(String token) {
        return parseClaims(token).getSubject();
    }

    private Claims parseClaims(String token) {
        try {
            return Jwts.parser().setSigningKey(secretKey)
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }
}
