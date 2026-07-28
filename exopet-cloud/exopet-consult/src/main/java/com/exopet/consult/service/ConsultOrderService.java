package com.exopet.consult.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.exopet.consult.entity.ConsultOrder;

import java.util.List;

public interface ConsultOrderService extends IService<ConsultOrder> {

    ConsultOrder getByIdOrThrow(Long id);

    ConsultOrder getByOrderNo(String orderNo);

    List<ConsultOrder> listByUserId(Long userId);
}
