package com.uade.lime.auth.model;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "denylisted_tokens", uniqueConstraints = @UniqueConstraint(name = "uk_denylisted_tokens_jti", columnNames = "jti"))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DenylistedToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 36)
    private String jti;

    @Column(nullable = false)
    private Instant expiresAt;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    public static DenylistedToken of(String jti, Instant expiresAt, Instant now) {
        DenylistedToken token = new DenylistedToken();
        token.jti = jti;
        token.expiresAt = expiresAt;
        token.createdAt = now;
        return token;
    }
}
