package com.exopet.user.controller;

import com.exopet.common.result.Result;
import com.exopet.user.entity.User;
import com.exopet.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "用户服务")
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Operation(summary = "获取用户信息")
    @GetMapping("/{id}")
    public Result<User> getUser(@PathVariable Long id) {
        return Result.success(userService.getByIdOrThrow(id));
    }

    @Operation(summary = "更新用户信息")
    @PutMapping("/{id}")
    public Result<Void> updateUser(@PathVariable Long id, @RequestBody User user) {
        user.setId(id);
        userService.updateById(user);
        return Result.success();
    }

    @Operation(summary = "获取用户列表")
    @GetMapping("/list")
    public Result<?> listUsers() {
        return Result.success(userService.list());
    }
}
