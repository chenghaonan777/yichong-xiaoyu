package com.exopet.hospital.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.exopet.hospital.entity.Hospital;

/**
 * 医院服务接口
 */
public interface HospitalService extends IService<Hospital> {

    /**
     * 根据ID查询，查不到抛异常
     */
    Hospital getByIdOrThrow(Long id);

    /**
     * 多条件分页查询（品类筛选 + 关键词搜索 + 按评分排序）
     */
    IPage<Hospital> listByCondition(int page, int size, String category, String keyword);
}
