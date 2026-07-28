package com.exopet.hospital.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.exopet.hospital.entity.Hospital;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @Author 23278
 * @Date 2026/7/27 18:50
 * @PackageName:com.exopet.hospital.mapper
 * @ClassName:HospitalMapper
 * @Description: TODO
 * @Version 1.0
 */
@Mapper
public interface HospitalMapper extends BaseMapper<Hospital> {

    /**
     * 多条件分页查询医院
     */
    IPage<Hospital> selectByCondition(Page<Hospital> page,
                                      @Param("category") String category,
                                      @Param("keyword") String keyword,
                                      @Param("status") Integer status);
}