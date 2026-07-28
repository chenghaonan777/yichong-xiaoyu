package com.exopet.hospital.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.exopet.hospital.entity.HospitalAppointment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

/**
 * 预约 Mapper
 */
@Mapper
public interface HospitalAppointmentMapper extends BaseMapper<HospitalAppointment> {

    /**
     * 按用户ID分页查询预约列表
     */
    IPage<HospitalAppointment> selectByUserId(Page<HospitalAppointment> page,
                                              @Param("userId") Long userId);

    /**
     * 按医院+日期查询预约
     */
    List<HospitalAppointment> selectByHospitalIdAndDate(@Param("hospitalId") Long hospitalId,
                                                        @Param("appointDate") LocalDate appointDate);
}