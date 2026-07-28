package com.exopet.auth.controller;

import com.exopet.auth.util.JwtUtil;
import com.exopet.common.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.TimeUnit;

@Tag(name = "认证中心")
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final JwtUtil jwtUtil;
    private final StringRedisTemplate redisTemplate;

    public AuthController(JwtUtil jwtUtil, StringRedisTemplate redisTemplate) {
        this.jwtUtil = jwtUtil;
        this.redisTemplate = redisTemplate;
    }

    @Operation(summary = "手机号+验证码登录")
    @PostMapping("/login")
    public Result<LoginVo> login(@Valid @RequestBody LoginReq req) {
        // TODO: 校验验证码 / 密码 → 从 Redis 中比对 code
        // 当前为开发阶段模拟，后续接入短信服务
        Long userId = 1L;
        String token = jwtUtil.generateToken(userId, req.phone());

        // Token存入Redis (7天过期)
        redisTemplate.opsForValue().set(
                "exopet:token:" + token,
                String.valueOf(userId),
                7, TimeUnit.DAYS);

        return Result.success(new LoginVo(token, userId));
    }

    @Operation(summary = "退出登录")
    @PostMapping("/logout")
    public Result<Void> logout(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        redisTemplate.delete("exopet:token:" + token);
        return Result.success();
    }

    @Operation(summary = "发送验证码")
    @PostMapping("/send-code")
    public Result<String> sendCode(@RequestParam String phone) {
        // TODO: 调用短信服务发送验证码
        return Result.success("验证码已发送");
    }

    public record LoginReq(
            @NotBlank(message = "手机号不能为空") String phone,
            String code,
            String password) {}
    public record LoginVo(String token, Long userId) {}
}
