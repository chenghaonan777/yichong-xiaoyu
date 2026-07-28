package com.exopet.consult.controller;

import com.exopet.common.result.Result;
import com.exopet.consult.entity.ConsultMessage;
import com.exopet.consult.service.ConsultMessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "问诊消息")
@RestController
@RequestMapping("/consult/message")
@RequiredArgsConstructor
public class ConsultMessageController {

    private final ConsultMessageService consultMessageService;

    @Operation(summary = "查询某问诊单的所有消息")
    @GetMapping("/list/{consultId}")
    public Result<List<ConsultMessage>> listByConsultId(@PathVariable Long consultId) {
        return Result.success(consultMessageService.listByConsultId(consultId));
    }

    @Operation(summary = "发送消息")
    @PostMapping
    public Result<ConsultMessage> send(@Valid @RequestBody ConsultMessage message) {
        consultMessageService.save(message);
        return Result.success(message);
    }
}
