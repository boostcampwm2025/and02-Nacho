package com.andlife.nachoserver.auth.jwt

import io.jsonwebtoken.Claims
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.util.Date
import javax.crypto.SecretKey

@Component
class JwtProvider(
    @Value("\${jwt.secret}")
    private val secretKeyString: String,

    @Value("\${jwt.access-token-expiration-ms}")
    private val accessTokenExpirationMs: Long,

    @Value("\${jwt.refresh-token-expiration-ms}")
    private val refreshTokenExpirationMs: Long
) {
    private val secretKey: SecretKey by lazy {
        Keys.hmacShaKeyFor(secretKeyString.toByteArray())
    }

    fun generateAccessToken(userId: Long): String {
        return generateToken(userId, accessTokenExpirationMs, TOKEN_TYPE_ACCESS)
    }

    fun generateRefreshToken(userId: Long): String {
        return generateToken(userId, refreshTokenExpirationMs, TOKEN_TYPE_REFRESH)
    }

    private fun generateToken(userId: Long, expirationMs: Long, tokenType: String): String {
        val now = Date()
        val expiration = Date(now.time + expirationMs)

        return Jwts.builder()
            .subject(userId.toString())
            .claim(CLAIM_TOKEN_TYPE, tokenType)
            .issuedAt(now)
            .expiration(expiration)
            .signWith(secretKey)
            .compact()
    }

    fun getUserId(token: String): Long {
        return getClaims(token).subject.toLong()
    }

    fun validateToken(token: String): Boolean {
        return try {
            getClaims(token)
            true
        } catch (e: Exception) {
            false
        }
    }

    fun isTokenExpired(token: String): Boolean {
        return try {
            getClaims(token)
            false
        } catch (e: ExpiredJwtException) {
            true
        } catch (e: Exception) {
            false
        }
    }

    fun isRefreshToken(token: String): Boolean {
        return try {
            val claims = getClaims(token)
            claims[CLAIM_TOKEN_TYPE] == TOKEN_TYPE_REFRESH
        } catch (e: Exception) {
            false
        }
    }

    private fun getClaims(token: String): Claims {
        return Jwts.parser()
            .verifyWith(secretKey)
            .build()
            .parseSignedClaims(token)
            .payload
    }

    companion object {
        private const val CLAIM_TOKEN_TYPE = "type"
        private const val TOKEN_TYPE_ACCESS = "access"
        private const val TOKEN_TYPE_REFRESH = "refresh"
    }
}
