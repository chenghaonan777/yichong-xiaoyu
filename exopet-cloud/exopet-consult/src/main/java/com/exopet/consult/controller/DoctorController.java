package com.exopet.consult.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.exopet.common.result.Result;
import com.exopet.consult.entity.Doctor;
import com.exopet.consult.service.DoctorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "医生管理")
@RestController
@RequestMapping("/consult/doctor")
@RequiredArgsConstructor
public class DoctorController {

    private final DoctorService doctorService;

    @Operation(summary = "根据ID查询医生")
    @GetMapping("/{id}")
    public Result<Doctor> getById(@PathVariable Long id) {
        return Result.success(doctorService.getByIdOrThrow(id));
    }

    @Operation(summary = "查询所有启用医生")
    @GetMapping("/list")
    public Result<List<Doctor>> list() {
        return Result.success(doctorService.lambdaQuery()
                .eq(Doctor::getStatus, 1)
                .orderByDesc(Doctor::getRating)
                .list());
    }

    @Operation(summary = "分页查询医生")
    @GetMapping("/list/page")
    public Result<IPage<Doctor>> page(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页条数") @RequestParam(defaultValue = "20") int size) {
        return Result.success(doctorService.page(Page.of(page, size)));
    }
}
