package com.exopet.hospital.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.exopet.common.result.Result;
import com.exopet.hospital.entity.Hospital;
import com.exopet.hospital.service.HospitalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "医院管理")
@RestController
@RequestMapping("/hospital")
@RequiredArgsConstructor
public class HospitalController {

    private final HospitalService hospitalService;

    @Operation(summary = "根据ID查询医院详情")
    @GetMapping("/{id}")
    public Result<Hospital> getById(@PathVariable Long id) {
        return Result.success(hospitalService.getByIdOrThrow(id));
    }

    @Operation(summary = "多条件分页查询医院（品类筛选+关键词搜索）")
    @GetMapping("/list")
    public Result<IPage<Hospital>> list(
            @Parameter(description = "接诊品类") @RequestParam(required = false) String category,
            @Parameter(description = "关键词(名称/地址)") @RequestParam(required = false) String keyword,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页条数") @RequestParam(defaultValue = "20") int size) {
        return Result.success(hospitalService.listByCondition(page, size, category, keyword));
    }
}
