package com.uade.lime.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.uade.lime.auth.model.DenylistedToken;

public interface DenylistedTokenRepository extends JpaRepository<DenylistedToken, Long> {

    boolean existsByJti(String jti);
}
