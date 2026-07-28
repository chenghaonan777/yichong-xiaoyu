package com.exopet.gateway.filter;

import com.exopet.common.constant.GlobalConstants;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * 全局鉴权过滤器 — 优先从 Redis 校验 JWT，Redis 不可用时降级为本地解析
 */
@Slf4j
@Component
public class AuthGlobalFilter implements GlobalFilter, Ordered {

    private final StringRedisTemplate redisTemplate;
    private final SecretKey signingKey;

    /**
     * 白名单：无需登录即可访问
     */
    private static final List<String> WHITE_LIST = List.of(
            "/auth/login", "/auth/register", "/auth/send-code",
            "/hospital/list", "/hospital/detail",
            "/api/ai/diagnose",
            "/v3/api-docs", "/swagger-ui", "/doc.html",
            "/actuator"
    );

    public AuthGlobalFilter(StringRedisTemplate redisTemplate,
                            @Value("${jwt.secret}") String jwtSecret) {
        this.redisTemplate = redisTemplate;
        this.signingKey = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();

        // 白名单放行
        if (isWhiteList(path)) {
            return chain.filter(exchange);
        }

        // 从请求头获取 Token
        String token = request.getHeaders().getFirst(GlobalConstants.TOKEN_HEADER);
        if (token == null || !token.startsWith(GlobalConstants.TOKEN_PREFIX)) {
            log.warn("Token缺失: {}", path);
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        String realToken = token.substring(7);
        String userId = resolveUserId(realToken);

        if (userId == null) {
            log.warn("Token无效或已过期: {}", path);
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        // 将 userId 传递到下游服务
        ServerHttpRequest mutatedRequest = request.mutate()
                .header(GlobalConstants.USER_ID_KEY, userId)
                .build();

        return chain.filter(exchange.mutate().request(mutatedRequest).build());
    }

    /**
     * 解析用户ID：优先 Redis 校验，Redis 不可用时降级为 JWT 本地解析
     */
    private String resolveUserId(String token) {
        // 1. 优先从 Redis 校验
        try {
            String userId = redisTemplate.opsForValue()
                    .get(GlobalConstants.REDIS_TOKEN_KEY + token);
            if (userId != null) {
                return userId;
            }
        } catch (Exception e) {
            log.warn("Redis不可用，降级为JWT本地解析: {}", e.getMessage());
        }

        // 2. Redis 不可用或 Token 不在 Redis 中 → JWT 本地解析
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(signingKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            return claims.getSubject();
        } catch (Exception e) {
            log.warn("JWT解析失败: {}", e.getMessage());
            return null;
        }
    }

    private boolean isWhiteList(String path) {
        return WHITE_LIST.stream().anyMatch(path::startsWith);
    }

    @Override
    public int getOrder() {
        return -1;
    }
}
