package com.liuyu.usercenter.common;

public enum ErrorCode {
    SUCCESS(0,"ok",""),
    PAEAMS_ERROE(40000,"请求参数错误",""),
    NULL_ERROR(40001,"请求数据为空",""),
    NOT_LOGIN(40100,"未登录",""),
    NO_AUTH(40101,"无权限",""),
    SYSTEM_ERROR(50000,"系统错误","");

    private final int code;
    /**
     * 状态码信息
     */
    private final String message;
    /**
     * 状态码描述
     */
    private final String describe;

    ErrorCode(int code, String message, String describe) {
        this.code = code;
        this.message = message;
        this.describe = describe;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public String getDescribe() {
        return describe;
    }
}
