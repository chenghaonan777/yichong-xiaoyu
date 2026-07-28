package com.exopet.user.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.exopet.common.exception.BusinessException;
import com.exopet.common.result.ResultCode;
import com.exopet.user.entity.User;
import com.exopet.user.mapper.UserMapper;
import com.exopet.user.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Override
    public User getByPhone(String phone) {
        return baseMapper.selectByPhone(phone);
    }

    @Override
    public User getByIdOrThrow(Long id) {
        User user = getById(id);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }
        return user;
    }

    @Override
    public boolean isPhoneExist(String phone) {
        return baseMapper.countByPhone(phone) > 0;
    }
}
