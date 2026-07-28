package com.exopet.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * JWT 签发 + 验证 快速测试，不需要启动 Spring
 */
public class JwtTest {

    static final String SECRET = "ExoPet2026SecretKeyForJWTTokenGenerationMustBeLongEnough";
    static final SecretKey KEY = Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));

    public static void main(String[] args) {
        // ===== 1. 签发 Token =====
        String token = Jwts.builder()
                .subject("1")                              // userId
                .claim("phone", "132****1697")             // 手机号
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 7 * 24 * 3600 * 1000)) // 7天
                .signWith(KEY)
                .compact();

        System.out.println("=== 签发 Token ===");
        System.out.println("Token: " + token);
        System.out.println();

        // ===== 2. 验证 Token =====
        System.out.println("=== 验证 Token ===");
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(KEY)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            System.out.println("✅ 验证通过");
            System.out.println("  userId: " + claims.getSubject());
            System.out.println("  phone:  " + claims.get("phone"));
            System.out.println("  签发:   " + claims.getIssuedAt());
            System.out.println("  过期:   " + claims.getExpiration());
        } catch (Exception e) {
            System.out.println("❌ 验证失败: " + e.getMessage());
        }
        System.out.println();

        // ===== 3. 伪造 Token 测试 =====
        System.out.println("=== 伪造 Token 测试 ===");
        String fakeToken = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiI5OTkifQ.fake";
        try {
            Jwts.parser().verifyWith(KEY).build().parseSignedClaims(fakeToken);
            System.out.println("❌ 伪造的竟然通过了（不应该！）");
        } catch (Exception e) {
            System.out.println("✅ 伪造 Token 被正确拒绝: " + e.getClass().getSimpleName());
        }
    }
}
