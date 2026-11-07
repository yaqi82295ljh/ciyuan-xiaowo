package org.example.ljh.ciyuan.common;

import cn.dev33.satoken.exception.NotLoginException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NotLoginException.class)
    public Result<?> handleNotLogin(NotLoginException e) {
        return Result.error(401, "登录已过期，请重新登录");
    }
}
