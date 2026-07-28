package com.exopet.pet.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.exopet.pet.entity.MedicalCase;

import java.time.LocalDate;

public interface MedicalCaseService extends IService<MedicalCase> {

    MedicalCase getByIdOrThrow(Long id);

    IPage<MedicalCase> pageByPetId(int pageNum, int pageSize, Long petId);

    IPage<MedicalCase> pageByUserId(int pageNum, int pageSize, Long userId);

    IPage<MedicalCase> pageByCondition(int pageNum, int pageSize,
                                        Long petId, Long userId,
                                        String severity, Integer status,
                                        String keyword,
                                        LocalDate startDate, LocalDate endDate);
}
