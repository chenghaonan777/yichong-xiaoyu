package com.exopet.consult.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.exopet.consult.entity.ConsultOrder;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ConsultOrderMapper extends BaseMapper<ConsultOrder> {

    List<ConsultOrder> selectByUserId(@Param("userId") Long userId);

    ConsultOrder selectByOrderNo(@Param("orderNo") String orderNo);
}
