package com.exopet.pet.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.exopet.common.exception.BusinessException;
import com.exopet.common.result.ResultCode;
import com.exopet.pet.entity.HealthRecord;
import com.exopet.pet.mapper.HealthRecordMapper;
import com.exopet.pet.service.HealthRecordService;
import org.springframework.stereotype.Service;

/**
 * 健康记录服务实现
 */
@Service
public class HealthRecordServiceImpl extends ServiceImpl<HealthRecordMapper, HealthRecord>
        implements HealthRecordService {

    @Override
    public HealthRecord getByIdOrThrow(Long id) {
        HealthRecord record = getById(id);
        if (record == null) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }
        return record;
    }

    @Override
    public IPage<HealthRecord> pageByPetId(int pageNum, int pageSize, Long petId) {
        return lambdaQuery()
                .eq(HealthRecord::getPetId, petId)
                .orderByDesc(HealthRecord::getRecordDate)
                .orderByDesc(HealthRecord::getCreatedAt)
                .page(Page.of(pageNum, pageSize));
    }

    @Override
    public IPage<HealthRecord> pageByPetIdAndType(int pageNum, int pageSize, Long petId, String recordType) {
        return lambdaQuery()
                .eq(HealthRecord::getPetId, petId)
                .eq(HealthRecord::getRecordType, recordType)
                .orderByDesc(HealthRecord::getRecordDate)
                .page(Page.of(pageNum, pageSize));
    }
}
