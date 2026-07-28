package com.exopet.hospital.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.exopet.hospital.entity.HospitalReview;

/**
 * 医院评价服务接口
 */
public interface HospitalReviewService extends IService<HospitalReview> {

    /**
     * 按医院ID分页查询评价
     */
    IPage<HospitalReview> listByHospitalId(int page, int size, Long hospitalId);
}
