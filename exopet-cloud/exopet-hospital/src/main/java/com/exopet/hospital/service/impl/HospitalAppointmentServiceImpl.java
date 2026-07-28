package com.exopet.hospital.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.exopet.hospital.entity.HospitalAppointment;
import com.exopet.hospital.mapper.HospitalAppointmentMapper;
import com.exopet.hospital.service.HospitalAppointmentService;
import com.exopet.hospital.service.HospitalService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 医院预约服务实现
 */
@Service
@RequiredArgsConstructor
public class HospitalAppointmentServiceImpl extends ServiceImpl<HospitalAppointmentMapper, HospitalAppointment>
        implements HospitalAppointmentService {

    private final HospitalService hospitalService;

    @Override
    public HospitalAppointment createAppointment(HospitalAppointment appointment) {
        // 校验医院是否存在
        hospitalService.getByIdOrThrow(appointment.getHospitalId());
        // 默认待确认
        appointment.setStatus(0);
        save(appointment);
        return appointment;
    }

    @Override
    public IPage<HospitalAppointment> listByUserId(int page, int size, Long userId) {
        return baseMapper.selectByUserId(Page.of(page, size), userId);
    }
}
