package com.exopet.hospital.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.exopet.common.result.Result;
import com.exopet.hospital.entity.HospitalReview;
import com.exopet.hospital.service.HospitalReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "医院评价")
@RestController
@RequestMapping("/hospital/review")
@RequiredArgsConstructor
public class HospitalReviewController {

    private final HospitalReviewService hospitalReviewService;

    @Operation(summary = "提交医院评价")
    @PostMapping
    public Result<HospitalReview> create(@Valid @RequestBody HospitalReview review) {
        hospitalReviewService.save(review);
        return Result.success(review);
    }

    @Operation(summary = "分页查询医院评价")
    @GetMapping("/list/{hospitalId}")
    public Result<IPage<HospitalReview>> listByHospitalId(
            @PathVariable Long hospitalId,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页条数") @RequestParam(defaultValue = "20") int size) {
        return Result.success(hospitalReviewService.listByHospitalId(page, size, hospitalId));
    }
}
