package com.exopet.pet.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.exopet.pet.entity.ReminderPlan;

public interface ReminderPlanService extends IService<ReminderPlan> {

    ReminderPlan getByIdOrThrow(Long id);

    IPage<ReminderPlan> pageByPetId(int pageNum, int pageSize, Long petId);

    IPage<ReminderPlan> pagePendingByUserId(int pageNum, int pageSize, Long userId);
}
