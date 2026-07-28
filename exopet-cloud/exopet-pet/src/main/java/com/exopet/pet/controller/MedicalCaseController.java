package com.exopet.pet.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.exopet.common.result.Result;
import com.exopet.pet.entity.MedicalCase;
import com.exopet.pet.service.MedicalCaseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

/**
 * 病例管理 Controller
 */
@Tag(name = "病例管理")
@RestController
@RequestMapping("/pet/case")
@RequiredArgsConstructor
public class MedicalCaseController {

    private final MedicalCaseService medicalCaseService;

    // ==================== 查询 ====================

    @Operation(summary = "根据ID查询病例详情")
    @GetMapping("/{id}")
    public Result<MedicalCase> getById(@PathVariable Long id) {
        return Result.success(medicalCaseService.getByIdOrThrow(id));
    }

    @Operation(summary = "分页查询某宠物的病例")
    @GetMapping("/list/by-pet/{petId}")
    public Result<IPage<MedicalCase>> listByPetId(
            @PathVariable Long petId,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页条数") @RequestParam(defaultValue = "20") int size) {
        return Result.success(medicalCaseService.pageByPetId(page, size, petId));
    }

    @Operation(summary = "分页查询某用户的病例")
    @GetMapping("/list/by-user/{userId}")
    public Result<IPage<MedicalCase>> listByUserId(
            @PathVariable Long userId,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页条数") @RequestParam(defaultValue = "20") int size) {
        return Result.success(medicalCaseService.pageByUserId(page, size, userId));
    }

    @Operation(summary = "多条件分页组合查询病例")
    @GetMapping("/list")
    public Result<IPage<MedicalCase>> listByCondition(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页条数") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "宠物ID") @RequestParam(required = false) Long petId,
            @Parameter(description = "用户ID") @RequestParam(required = false) Long userId,
            @Parameter(description = "严重程度: MILD/MODERATE/SEVERE") @RequestParam(required = false) String severity,
            @Parameter(description = "状态: 0就诊中 1已康复 2复诊中") @RequestParam(required = false) Integer status,
            @Parameter(description = "关键词(匹配标题/症状/诊断)") @RequestParam(required = false) String keyword,
            @Parameter(description = "就诊开始日期") @RequestParam(required = false) LocalDate startDate,
            @Parameter(description = "就诊结束日期") @RequestParam(required = false) LocalDate endDate) {

        return Result.success(medicalCaseService.pageByCondition(
                page, size, petId, userId, severity, status, keyword, startDate, endDate));
    }

    // ==================== 新增 ====================

    @Operation(summary = "新增病例")
    @PostMapping
    public Result<MedicalCase> create(@Valid @RequestBody MedicalCase medicalCase) {
        medicalCaseService.save(medicalCase);
        return Result.success(medicalCase);
    }

    // ==================== 更新 ====================

    @Operation(summary = "更新病例")
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody MedicalCase medicalCase) {
        medicalCase.setId(id);
        medicalCaseService.updateById(medicalCase);
        return Result.success();
    }

    // ==================== 删除 ====================

    @Operation(summary = "删除病例")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        medicalCaseService.getByIdOrThrow(id);
        medicalCaseService.removeById(id);
        return Result.success();
    }
}
