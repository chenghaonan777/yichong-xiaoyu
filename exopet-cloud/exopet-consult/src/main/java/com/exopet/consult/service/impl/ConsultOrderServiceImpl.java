package com.exopet.consult.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.exopet.common.exception.BusinessException;
import com.exopet.common.result.ResultCode;
import com.exopet.consult.entity.ConsultOrder;
import com.exopet.consult.mapper.ConsultOrderMapper;
import com.exopet.consult.service.ConsultOrderService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ConsultOrderServiceImpl extends ServiceImpl<ConsultOrderMapper, ConsultOrder>
        implements ConsultOrderService {

    @Override
    public ConsultOrder getByIdOrThrow(Long id) {
        ConsultOrder order = getById(id);
        if (order == null) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }
        return order;
    }

    @Override
    public ConsultOrder getByOrderNo(String orderNo) {
        return baseMapper.selectByOrderNo(orderNo);
    }

    @Override
    public List<ConsultOrder> listByUserId(Long userId) {
        return baseMapper.selectByUserId(userId);
    }
}
