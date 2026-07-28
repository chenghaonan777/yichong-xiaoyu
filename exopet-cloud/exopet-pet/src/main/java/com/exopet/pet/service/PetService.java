package com.exopet.pet.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.exopet.pet.entity.Pet;

import java.util.List;

public interface PetService extends IService<Pet> {

    Pet getByIdOrThrow(Long id);

    List<Pet> listByUserId(Long userId);

    IPage<Pet> pageByUserId(int pageNum, int pageSize, Long userId);
}
