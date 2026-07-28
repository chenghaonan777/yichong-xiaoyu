package com.exopet.consult.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.exopet.consult.entity.ConsultMessage;
import com.exopet.consult.mapper.ConsultMessageMapper;
import com.exopet.consult.service.ConsultMessageService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ConsultMessageServiceImpl extends ServiceImpl<ConsultMessageMapper, ConsultMessage>
        implements ConsultMessageService {

    @Override
    public List<ConsultMessage> listByConsultId(Long consultId) {
        return baseMapper.selectByConsultId(consultId);
    }
}
