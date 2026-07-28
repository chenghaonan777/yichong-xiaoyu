package com.exopet.pet.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.exopet.common.exception.BusinessException;
import com.exopet.common.result.ResultCode;
import com.exopet.pet.entity.Pet;
import com.exopet.pet.mapper.PetMapper;
import com.exopet.pet.service.PetService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 宠物服务实现
 */
@Service
public class PetServiceImpl extends ServiceImpl<PetMapper, Pet> implements PetService {

    @Override
    public Pet getByIdOrThrow(Long id) {
        Pet pet = getById(id);
        if (pet == null) {
            throw new BusinessException(ResultCode.PET_NOT_FOUND);
        }
        return pet;
    }

    @Override
    public List<Pet> listByUserId(Long userId) {
        return lambdaQuery()
                .eq(Pet::getUserId, userId)
                .orderByDesc(Pet::getIsCurrent)
                .orderByDesc(Pet::getCreatedAt)
                .list();
    }

    @Override
    public IPage<Pet> pageByUserId(int pageNum, int pageSize, Long userId) {
        return lambdaQuery()
                .eq(Pet::getUserId, userId)
                .orderByDesc(Pet::getIsCurrent)
                .orderByDesc(Pet::getCreatedAt)
                .page(Page.of(pageNum, pageSize));
    }
}
