package com.exopet.hospital.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.exopet.hospital.entity.HospitalAppointment;

/**
 * 医院预约服务接口
 */
public interface HospitalAppointmentService extends IService<HospitalAppointment> {

    /**
     * 创建预约（校验医院是否存在）
     */
    HospitalAppointment createAppointment(HospitalAppointment appointment);

    /**
     * 分页查询用户预约列表
     */
    IPage<HospitalAppointment> listByUserId(int page, int size, Long userId);

}
