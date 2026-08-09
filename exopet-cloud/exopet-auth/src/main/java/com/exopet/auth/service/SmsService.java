package com.exopet.auth.service;

/**
 * 短信验证码服务
 */
public interface SmsService {

    /**
     * 发送验证码
     */
    void sendCode(String phone);

    /**
     * 校验验证码
     */
    boolean checkCode(String phone, String code);
}
