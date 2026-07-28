package com.exopet.hospital.controller;

import com.exopet.common.result.Result;
import com.exopet.hospital.entity.HospitalAppointment;
import com.exopet.hospital.service.HospitalAppointmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "医院预约")
@RestController
@RequestMapping("/hospital/appointment")
@RequiredArgsConstructor
public class HospitalAppointmentController {

    private final HospitalAppointmentService hospitalAppointmentService;

    @Operation(summary = "预约就诊")
    @PostMapping
    public Result<HospitalAppointment> create(@Valid @RequestBody HospitalAppointment appointment) {
        return Result.success(hospitalAppointmentService.createAppointment(appointment));
    }

    @Operation(summary = "查询用户预约列表")
    @GetMapping("/list/{userId}")
    public Result<com.baomidou.mybatisplus.core.metadata.IPage<HospitalAppointment>> listByUserId(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return Result.success(hospitalAppointmentService.listByUserId(page, size, userId));
    }

}

