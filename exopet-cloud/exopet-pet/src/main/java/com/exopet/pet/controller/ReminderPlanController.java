package com.exopet.pet.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.exopet.common.result.Result;
import com.exopet.pet.entity.ReminderPlan;
import com.exopet.pet.service.ReminderPlanService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "提醒计划")
@RestController
@RequestMapping("/pet/reminder")
@RequiredArgsConstructor
public class ReminderPlanController {

    private final ReminderPlanService reminderPlanService;

    @Operation(summary = "根据ID查询提醒计划")
    @GetMapping("/{id}")
    public Result<ReminderPlan> getById(@PathVariable Long id) {
        return Result.success(reminderPlanService.getByIdOrThrow(id));
    }

    @Operation(summary = "分页查询某宠物的提醒计划")
    @GetMapping("/list/by-pet/{petId}")
    public Result<IPage<ReminderPlan>> listByPetId(
            @PathVariable Long petId,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页条数") @RequestParam(defaultValue = "20") int size) {
        return Result.success(reminderPlanService.pageByPetId(page, size, petId));
    }

    @Operation(summary = "查询某用户所有待处理的提醒")
    @GetMapping("/list/pending/{userId}")
    public Result<IPage<ReminderPlan>> listPending(
            @PathVariable Long userId,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页条数") @RequestParam(defaultValue = "20") int size) {
        return Result.success(reminderPlanService.pagePendingByUserId(page, size, userId));
    }

    @Operation(summary = "添加提醒计划")
    @PostMapping
    public Result<ReminderPlan> create(@Valid @RequestBody ReminderPlan plan) {
        reminderPlanService.save(plan);
        return Result.success(plan);
    }

    @Operation(summary = "更新提醒计划")
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody ReminderPlan plan) {
        plan.setId(id);
        reminderPlanService.updateById(plan);
        return Result.success();
    }

    @Operation(summary = "删除提醒计划")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        reminderPlanService.getByIdOrThrow(id);
        reminderPlanService.removeById(id);
        return Result.success();
    }
}
