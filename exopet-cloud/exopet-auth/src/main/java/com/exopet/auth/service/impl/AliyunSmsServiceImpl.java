package com.exopet.auth.service.impl;

import com.exopet.auth.service.SmsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 当前为开发调试模式，不依赖阿里云短信服务。
 * 验证码固定为 123456，所有手机号都能登录。
 * 后续对接阿里云短信时替换此类（参考 dypnsapi20170525 SDK）。
 */
@Slf4j
@Service
public class AliyunSmsServiceImpl implements SmsService {

    @Override
    public void sendCode(String phone) {
        log.info("【调试】手机号 {} 的验证码为 123456（固定值）", phone);
    }

    @Override
    public boolean checkCode(String phone, String code) {
        boolean pass = "123456".equals(code);
        log.info("【调试】手机号 {} 验证码 {} → {}", phone, code, pass ? "通过" : "失败");
        return pass;
    }
}
