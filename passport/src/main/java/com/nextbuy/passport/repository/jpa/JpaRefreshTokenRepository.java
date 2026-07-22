package com.nextbuy.passport.repository.jpa;

import com.nextbuy.passport.domain.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;

@Repository
public interface JpaRefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByTokenHash(String tokenHash);

    @Query("""
                SELECT rt FROM RefreshToken rt
                WHERE rt.tokenHash = :tokenHash
            """)
    Optional<RefreshToken> findByTokenHashIncludingRevoked(@Param("tokenHash") String tokenHash);


    @Modifying
    @Query("""
              UPDATE RefreshToken rt
              SET rt.revokedAt = :now
              WHERE rt.user.id = :userId
              AND rt.revokedAt IS NULL
              AND rt.expiresAt > :now
            """)
    void revokeAllActiveByUserId(@Param("userId") Long userId, @Param("now") Instant now);
}
