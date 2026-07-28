package com.exopet.consult.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.exopet.consult.entity.Doctor;

public interface DoctorService extends IService<Doctor> {

    Doctor getByIdOrThrow(Long id);
}
