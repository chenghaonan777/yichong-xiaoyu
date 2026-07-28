package com.exopet.hospital.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.exopet.common.exception.BusinessException;
import com.exopet.common.result.ResultCode;
import com.exopet.hospital.entity.Hospital;
import com.exopet.hospital.mapper.HospitalMapper;
import com.exopet.hospital.service.HospitalService;
import org.springframework.stereotype.Service;

/**
 * 医院服务实现
 */
@Service
public class HospitalServiceImpl extends ServiceImpl<HospitalMapper, Hospital> implements HospitalService {

    @Override
    public Hospital getByIdOrThrow(Long id) {
        Hospital hospital = getById(id);
        if (hospital == null) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }
        return hospital;
    }

    @Override
    public IPage<Hospital> listByCondition(int page, int size, String category, String keyword) {
        return baseMapper.selectByCondition(Page.of(page, size), category, keyword, 1);
    }
}
