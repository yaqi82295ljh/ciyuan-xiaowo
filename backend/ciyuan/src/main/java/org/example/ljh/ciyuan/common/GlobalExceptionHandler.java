package org.example.ljh.ciyuan.common;

import cn.dev33.satoken.exception.NotLoginException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NotLoginException.class)
    public Result<?> handleNotLogin(NotLoginException e) {
        //NotLoginException 自带的消息长这样："token 无效：f50307f4-1915-44f5-a95f-fcfd1ba15bc6"
        //这是给开发看的，页面上直接弹出来用户看不懂，也等于把登录票据打印在页面上。
        //所以这里不再拼 e.getMessage()，统一换成用户能看懂的一句话。
        return Result.error(401, "登录已过期，请重新登录");
    }
}
