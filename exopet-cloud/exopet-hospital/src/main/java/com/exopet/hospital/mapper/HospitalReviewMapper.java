package com.exopet.hospital.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.exopet.hospital.entity.HospitalReview;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @Author 23278
 * @Date 2026/7/27 18:55
 * @PackageName:com.exopet.hospital.mapper
 * @ClassName:HospitalReviewMapper
 * @Description: TODO
 * @Version 1.0
 */
@Mapper
public interface HospitalReviewMapper extends BaseMapper<HospitalReview> {

    /**
     * 按医院ID查询评价列表（分页，按时间倒序）
     */
    IPage<HospitalReview> selectByHospitalId(Page<HospitalReview> page,
                                             @Param("hospitalId") Long hospitalId);
}