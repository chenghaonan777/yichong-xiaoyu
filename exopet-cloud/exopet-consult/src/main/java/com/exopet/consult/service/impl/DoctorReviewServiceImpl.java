package com.exopet.consult.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.exopet.consult.entity.DoctorReview;
import com.exopet.consult.mapper.DoctorReviewMapper;
import com.exopet.consult.service.DoctorReviewService;
import org.springframework.stereotype.Service;

@Service
public class DoctorReviewServiceImpl extends ServiceImpl<DoctorReviewMapper, DoctorReview>
        implements DoctorReviewService {
}
