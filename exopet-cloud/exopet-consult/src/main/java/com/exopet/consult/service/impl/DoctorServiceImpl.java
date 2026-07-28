package com.exopet.consult.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.exopet.common.exception.BusinessException;
import com.exopet.common.result.ResultCode;
import com.exopet.consult.entity.Doctor;
import com.exopet.consult.mapper.DoctorMapper;
import com.exopet.consult.service.DoctorService;
import org.springframework.stereotype.Service;

@Service
public class DoctorServiceImpl extends ServiceImpl<DoctorMapper, Doctor> implements DoctorService {

    @Override
    public Doctor getByIdOrThrow(Long id) {
        Doctor doctor = getById(id);
        if (doctor == null) {
            throw new BusinessException(ResultCode.DOCTOR_NOT_FOUND);
        }
        return doctor;
    }
}
