package com.exopet.pet.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.exopet.pet.entity.MedicalCase;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;

/**
 * 病例 Mapper — BaseMapper 提供基础 CRUD，复杂查询走 XML
 */
@Mapper
public interface MedicalCaseMapper extends BaseMapper<MedicalCase> {

    /**
     * 按宠物ID分页查询病例
     */
    IPage<MedicalCase> selectByPetId(Page<MedicalCase> page, @Param("petId") Long petId);

    /**
     * 按用户ID分页查询病例
     */
    IPage<MedicalCase> selectByUserId(Page<MedicalCase> page, @Param("userId") Long userId);

    /**
     * 多条件分页组合查询
     */
    IPage<MedicalCase> selectByCondition(Page<MedicalCase> page,
                                         @Param("petId") Long petId,
                                         @Param("userId") Long userId,
                                         @Param("severity") String severity,
                                         @Param("status") Integer status,
                                         @Param("keyword") String keyword,
                                         @Param("startDate") LocalDate startDate,
                                         @Param("endDate") LocalDate endDate);
}
