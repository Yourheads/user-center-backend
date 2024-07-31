package com.liuyu.usercenter.utils;

import com.liuyu.usercenter.common.BaseResponse;
import com.liuyu.usercenter.common.ErrorCode;

public class ResponseUtils {
    /**
     * 成功
     * @param data
     * @return
     * @param <T>
     */
    public static <T> BaseResponse<T> success(T data){

        return new BaseResponse<>(0,data,"ok");
    }

    /**
     * 失败
     * @param errorCode
     * @return
     * @param <T>
     */
    public static <T> BaseResponse<T> error(ErrorCode errorCode){
        return new BaseResponse<>(errorCode);
    }

    /**
     * 失败
     * @param code
     * @param message
     * @param description
     * @return
     * @param <T>
     */
    public static <T> BaseResponse<T> error(int code,String message,String description){
        return new BaseResponse<>(code,null,message,description);
    }

    public static <T> BaseResponse<T> error(ErrorCode errorCode,String message,String descrption){
        return new BaseResponse<>(errorCode,message,descrption);
    }

}
