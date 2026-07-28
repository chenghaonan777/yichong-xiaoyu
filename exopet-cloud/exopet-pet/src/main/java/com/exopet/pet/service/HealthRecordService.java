package com.exopet.pet.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.exopet.pet.entity.HealthRecord;

public interface HealthRecordService extends IService<HealthRecord> {

    HealthRecord getByIdOrThrow(Long id);

    IPage<HealthRecord> pageByPetId(int pageNum, int pageSize, Long petId);

    IPage<HealthRecord> pageByPetIdAndType(int pageNum, int pageSize, Long petId, String recordType);
}
