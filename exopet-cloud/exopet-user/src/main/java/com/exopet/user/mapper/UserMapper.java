package com.exopet.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.exopet.user.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserMapper extends BaseMapper<User> {

    /** 根据手机号查询用户 */
    User selectByPhone(@Param("phone") String phone);

    /** 查询手机号是否已存在 */
    int countByPhone(@Param("phone") String phone);
}
