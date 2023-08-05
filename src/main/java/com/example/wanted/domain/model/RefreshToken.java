package com.example.wanted.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@EntityListeners(AuditingEntityListener.class)
public class RefreshToken {
    @Id
    private String email;

    @Column(nullable = false, unique = true)
    private String refreshToken;

    public void setRefreshToken(String refreshToken){
        this.refreshToken = refreshToken;
    }
}
