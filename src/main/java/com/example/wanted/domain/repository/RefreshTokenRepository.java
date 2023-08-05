package com.example.wanted.domain.repository;

import com.example.wanted.domain.model.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, String> {
    Optional<RefreshToken> findByRefreshToken(String refreshToken);
}
