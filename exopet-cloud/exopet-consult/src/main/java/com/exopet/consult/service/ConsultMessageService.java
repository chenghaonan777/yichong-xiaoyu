package com.exopet.consult.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.exopet.consult.entity.ConsultMessage;

import java.util.List;

public interface ConsultMessageService extends IService<ConsultMessage> {

    List<ConsultMessage> listByConsultId(Long consultId);
}
