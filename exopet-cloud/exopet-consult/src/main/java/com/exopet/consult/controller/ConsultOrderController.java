package com.exopet.consult.controller;

import com.exopet.common.result.Result;
import com.exopet.consult.entity.ConsultOrder;
import com.exopet.consult.service.ConsultOrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "问诊订单")
@RestController
@RequestMapping("/consult/order")
@RequiredArgsConstructor
public class ConsultOrderController {

    private final ConsultOrderService consultOrderService;

    @Operation(summary = "根据问诊单号查询")
    @GetMapping("/{orderNo}")
    public Result<ConsultOrder> getByOrderNo(@PathVariable String orderNo) {
        ConsultOrder order = consultOrderService.getByOrderNo(orderNo);
        if (order == null) {
            return Result.failed("问诊单不存在");
        }
        return Result.success(order);
    }

    @Operation(summary = "查询某用户的问诊记录")
    @GetMapping("/list/{userId}")
    public Result<List<ConsultOrder>> listByUserId(@PathVariable Long userId) {
        return Result.success(consultOrderService.listByUserId(userId));
    }

    @Operation(summary = "发起问诊")
    @PostMapping
    public Result<ConsultOrder> create(@Valid @RequestBody ConsultOrder order) {
        consultOrderService.save(order);
        return Result.success(order);
    }

    @Operation(summary = "更新问诊单")
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody ConsultOrder order) {
        order.setId(id);
        consultOrderService.updateById(order);
        return Result.success();
    }
}
