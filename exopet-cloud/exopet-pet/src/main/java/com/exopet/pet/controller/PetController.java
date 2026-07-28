package com.exopet.pet.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.exopet.common.result.Result;
import com.exopet.pet.entity.Pet;
import com.exopet.pet.service.PetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "宠物管理")
@RestController
@RequestMapping("/pet")
@RequiredArgsConstructor
public class PetController {

    private final PetService petService;

    @Operation(summary = "根据ID查询宠物详情")
    @GetMapping("/{id}")
    public Result<Pet> getById(@PathVariable Long id) {
        return Result.success(petService.getByIdOrThrow(id));
    }

    @Operation(summary = "查询某用户的所有宠物")
    @GetMapping("/list/by-user/{userId}")
    public Result<List<Pet>> listByUserId(@PathVariable Long userId) {
        return Result.success(petService.listByUserId(userId));
    }

    @Operation(summary = "分页查询某用户的宠物")
    @GetMapping("/list")
    public Result<IPage<Pet>> pageByUserId(
            @Parameter(description = "用户ID") @RequestParam Long userId,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页条数") @RequestParam(defaultValue = "20") int size) {
        return Result.success(petService.pageByUserId(page, size, userId));
    }

    @Operation(summary = "添加宠物")
    @PostMapping
    public Result<Pet> create(@Valid @RequestBody Pet pet) {
        petService.save(pet);
        return Result.success(pet);
    }

    @Operation(summary = "更新宠物信息")
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody Pet pet) {
        pet.setId(id);
        petService.updateById(pet);
        return Result.success();
    }

    @Operation(summary = "删除宠物")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        petService.getByIdOrThrow(id);
        petService.removeById(id);
        return Result.success();
    }
}
