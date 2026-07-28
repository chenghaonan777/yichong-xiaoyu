package com.exopet.pet.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.exopet.common.result.Result;
import com.exopet.pet.entity.HealthRecord;
import com.exopet.pet.service.HealthRecordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "健康记录")
@RestController
@RequestMapping("/pet/health-record")
@RequiredArgsConstructor
public class HealthRecordController {

    private final HealthRecordService healthRecordService;

    @Operation(summary = "根据ID查询健康记录")
    @GetMapping("/{id}")
    public Result<HealthRecord> getById(@PathVariable Long id) {
        return Result.success(healthRecordService.getByIdOrThrow(id));
    }

    @Operation(summary = "分页查询某宠物的健康记录")
    @GetMapping("/list/by-pet/{petId}")
    public Result<IPage<HealthRecord>> listByPetId(
            @PathVariable Long petId,
            @Parameter(description = "记录类型(可选): vaccine/deworm/checkup/medication/weight/consult")
                @RequestParam(required = false) String recordType,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页条数") @RequestParam(defaultValue = "20") int size) {

        if (recordType != null) {
            return Result.success(healthRecordService.pageByPetIdAndType(page, size, petId, recordType));
        }
        return Result.success(healthRecordService.pageByPetId(page, size, petId));
    }

    @Operation(summary = "添加健康记录")
    @PostMapping
    public Result<HealthRecord> create(@Valid @RequestBody HealthRecord record) {
        healthRecordService.save(record);
        return Result.success(record);
    }

    @Operation(summary = "更新健康记录")
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody HealthRecord record) {
        record.setId(id);
        healthRecordService.updateById(record);
        return Result.success();
    }

    @Operation(summary = "删除健康记录")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        healthRecordService.getByIdOrThrow(id);
        healthRecordService.removeById(id);
        return Result.success();
    }
}
