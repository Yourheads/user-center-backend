package com.liuyu.usercenter.exception;

import com.liuyu.usercenter.common.BaseResponse;
import com.liuyu.usercenter.common.ErrorCode;
import com.liuyu.usercenter.utils.ResponseUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandle {
    @ExceptionHandler(BusinessException.class)
    public BaseResponse businessExceptionHandle(BusinessException e){
        log.error("businessException:"+e.getMessage(),e);
        return ResponseUtils.error(e.getCode(),e.getMessage(), e.getDescription());
    }
    @ExceptionHandler(RuntimeException.class)
    public BaseResponse runtimeExceptionHandle(RuntimeException e){
        log.error("runtimeException",e);
        return ResponseUtils.error(ErrorCode.SYSTEM_ERROR,e.getMessage(),"");
    }
}
