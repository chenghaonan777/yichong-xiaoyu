package com.exopet.pet.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.exopet.common.exception.BusinessException;
import com.exopet.common.result.ResultCode;
import com.exopet.pet.entity.MedicalCase;
import com.exopet.pet.mapper.MedicalCaseMapper;
import com.exopet.pet.service.MedicalCaseService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

/**
 * 病例服务实现
 */
@Service
public class MedicalCaseServiceImpl extends ServiceImpl<MedicalCaseMapper, MedicalCase>
        implements MedicalCaseService {

    @Override
    public MedicalCase getByIdOrThrow(Long id) {
        MedicalCase mc = getById(id);
        if (mc == null) {
            throw new BusinessException(ResultCode.CASE_NOT_FOUND);
        }
        return mc;
    }

    @Override
    public IPage<MedicalCase> pageByPetId(int pageNum, int pageSize, Long petId) {
        return baseMapper.selectByPetId(Page.of(pageNum, pageSize), petId);
    }

    @Override
    public IPage<MedicalCase> pageByUserId(int pageNum, int pageSize, Long userId) {
        return baseMapper.selectByUserId(Page.of(pageNum, pageSize), userId);
    }

    @Override
    public IPage<MedicalCase> pageByCondition(int pageNum, int pageSize,
                                               Long petId, Long userId,
                                               String severity, Integer status,
                                               String keyword,
                                               LocalDate startDate, LocalDate endDate) {
        return baseMapper.selectByCondition(Page.of(pageNum, pageSize),
                petId, userId, severity, status, keyword, startDate, endDate);
    }
}
