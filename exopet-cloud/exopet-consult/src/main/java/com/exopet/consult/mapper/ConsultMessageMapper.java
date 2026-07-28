package com.exopet.consult.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.exopet.consult.entity.ConsultMessage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ConsultMessageMapper extends BaseMapper<ConsultMessage> {

    List<ConsultMessage> selectByConsultId(@Param("consultId") Long consultId);
}
