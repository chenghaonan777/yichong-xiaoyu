package com.exopet.pet.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.exopet.common.exception.BusinessException;
import com.exopet.common.result.ResultCode;
import com.exopet.pet.entity.ReminderPlan;
import com.exopet.pet.mapper.ReminderPlanMapper;
import com.exopet.pet.service.ReminderPlanService;
import org.springframework.stereotype.Service;

/**
 * 提醒计划服务实现
 */
@Service
public class ReminderPlanServiceImpl extends ServiceImpl<ReminderPlanMapper, ReminderPlan>
        implements ReminderPlanService {

    @Override
    public ReminderPlan getByIdOrThrow(Long id) {
        ReminderPlan plan = getById(id);
        if (plan == null) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }
        return plan;
    }

    @Override
    public IPage<ReminderPlan> pageByPetId(int pageNum, int pageSize, Long petId) {
        return lambdaQuery()
                .eq(ReminderPlan::getPetId, petId)
                .orderByAsc(ReminderPlan::getRemindDate)
                .page(Page.of(pageNum, pageSize));
    }

    @Override
    public IPage<ReminderPlan> pagePendingByUserId(int pageNum, int pageSize, Long userId) {
        return lambdaQuery()
                .eq(ReminderPlan::getUserId, userId)
                .eq(ReminderPlan::getStatus, 0)
                .orderByAsc(ReminderPlan::getRemindDate)
                .page(Page.of(pageNum, pageSize));
    }
}
