package com.exopet.consult.controller;

import com.exopet.common.result.Result;
import com.exopet.consult.entity.DoctorReview;
import com.exopet.consult.service.DoctorReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "医生评价")
@RestController
@RequestMapping("/consult/review")
@RequiredArgsConstructor
public class DoctorReviewController {

    private final DoctorReviewService doctorReviewService;

    @Operation(summary = "新增评价")
    @PostMapping
    public Result<DoctorReview> create(@Valid @RequestBody DoctorReview review) {
        doctorReviewService.save(review);
        return Result.success(review);
    }
}
