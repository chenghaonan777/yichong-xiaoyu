package com.exopet.hospital.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.exopet.hospital.entity.HospitalReview;
import com.exopet.hospital.mapper.HospitalReviewMapper;
import com.exopet.hospital.service.HospitalReviewService;
import org.springframework.stereotype.Service;

/**
 * 医院评价服务实现
 */
@Service
public class HospitalReviewServiceImpl extends ServiceImpl<HospitalReviewMapper, HospitalReview>
        implements HospitalReviewService {

    @Override
    public IPage<HospitalReview> listByHospitalId(int page, int size, Long hospitalId) {
        return baseMapper.selectByHospitalId(Page.of(page, size), hospitalId);
    }
}
