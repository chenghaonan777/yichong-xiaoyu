package com.exopet.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.exopet.user.entity.User;

public interface UserService extends IService<User> {

    User getByPhone(String phone);

    User getByIdOrThrow(Long id);

    boolean isPhoneExist(String phone);
}
