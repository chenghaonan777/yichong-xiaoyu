package com.exopet.pet.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.exopet.pet.entity.Pet;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PetMapper extends BaseMapper<Pet> {
}
