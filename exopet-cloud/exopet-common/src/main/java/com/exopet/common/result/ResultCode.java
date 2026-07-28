package com.exopet.common.result;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 响应码枚举
 */
@Getter
@AllArgsConstructor
public enum ResultCode {

    SUCCESS(200, "操作成功"),
    FAILED(500, "操作失败"),
    VALIDATE_FAILED(400, "参数校验失败"),
    UNAUTHORIZED(401, "未登录或Token已过期"),
    FORBIDDEN(403, "无权限访问"),
    NOT_FOUND(404, "资源不存在"),

    // 业务异常
    USER_NOT_FOUND(1001, "用户不存在"),
    USER_PHONE_EXIST(1002, "手机号已注册"),
    PASSWORD_ERROR(1003, "密码错误"),
    PRODUCT_NOT_FOUND(2001, "商品不存在"),
    STOCK_NOT_ENOUGH(2002, "库存不足"),
    ORDER_NOT_FOUND(3001, "订单不存在"),
    ORDER_CANNOT_CANCEL(3002, "订单不可取消"),
    PET_NOT_FOUND(4001, "宠物不存在"),
    CASE_NOT_FOUND(4002, "病例不存在"),
    DOCTOR_NOT_FOUND(5001, "医生不存在"),
    COUPON_NOT_FOUND(6001, "优惠券不存在"),
    COUPON_EXPIRED(6002, "优惠券已过期"),
    MEMBER_EXPIRED(6003, "会员已过期");

    private final int code;
    private final String msg;
}
