package com.exopet.auth.controller;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.exopet.auth.service.SmsService;
import com.exopet.auth.util.JwtUtil;
import com.exopet.common.result.Result;
import com.exopet.user.entity.User;
import com.exopet.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.TimeUnit;

@Slf4j
@Tag(name = "认证中心")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final JwtUtil jwtUtil;
    private final StringRedisTemplate redisTemplate;
    private final SmsService smsService;
    private final UserService userService;

    @Operation(summary = "发送短信验证码")
    @PostMapping("/send-code")
    @SentinelResource(value = "authSendCode", blockHandler = "sendCodeBlockHandler")
    public Result<Void> sendCode(@Valid @RequestBody SendCodeReq req) {
        smsService.sendCode(req.getPhone());
        return Result.success();
    }

    public Result<Void> sendCodeBlockHandler(SendCodeReq req, BlockException e) {
        log.warn("发送验证码被限流: {}", e.getMessage());
        return Result.failed("验证码发送过于频繁，请稍后再试");
    }

    @Operation(summary = "手机号+验证码登录（自动注册）")
    @PostMapping("/login")
    @SentinelResource(value = "authLogin", blockHandler = "loginBlockHandler")
    public Result<LoginVo> login(@Valid @RequestBody LoginReq req) {
        // 1. 校验验证码
        boolean pass = smsService.checkCode(req.getPhone(), req.getCode());
        if (!pass) {
            return Result.failed(401, "验证码错误或已失效");
        }

        // 2. 查用户，不存在则自动注册
        User user = userService.getByPhone(req.getPhone());
        if (user == null) {
            user = new User();
            user.setPhone(req.getPhone());
            user.setNickname("用户" + req.getPhone().substring(7));
            userService.save(user);
            log.info("新用户自动注册 phone={} id={}", req.getPhone(), user.getId());
        }

        // 3. 签发 JWT Token
        String token = jwtUtil.generateToken(user.getId(), req.getPhone());

        // 4. Token 存入 Redis（7天过期）
        redisTemplate.opsForValue().set(
                "exopet:token:" + token,
                String.valueOf(user.getId()),
                7, TimeUnit.DAYS);

        return Result.success(new LoginVo(token, user.getId()));
    }

    public Result<LoginVo> loginBlockHandler(LoginReq req, BlockException e) {
        log.warn("登录接口被限流: {}", e.getMessage());
        return Result.failed("登录请求过于频繁，请稍后再试");
    }

    @Operation(summary = "退出登录")
    @PostMapping("/logout")
    public Result<Void> logout(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        redisTemplate.delete("exopet:token:" + token);
        return Result.success();
    }

    // ---- 请求/响应体 ----

    @Data
    public static class SendCodeReq {
        @NotBlank(message = "手机号不能为空")
        @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
        private String phone;
    }

    @Data
    public static class LoginReq {
        @NotBlank(message = "手机号不能为空")
        @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
        private String phone;

        @NotBlank(message = "验证码不能为空")
        @Pattern(regexp = "^\\d{4,6}$", message = "验证码格式不正确")
        private String code;
    }

    public record LoginVo(String token, Long userId) {}
}
