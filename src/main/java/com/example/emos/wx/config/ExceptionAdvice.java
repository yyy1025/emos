package com.example.emos.wx.config;

import com.example.emos.wx.exception.EmosException;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.UnauthorizedException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestControllerAdvice
public class ExceptionAdvice {
    @ResponseBody//返回的错误消息需要加到响应里面，所以必须加上这个注解
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public String ValidExceptionHandler(Exception e){
        log.error("执行异常"+e);
        //后端校验的异常消息
        if(e instanceof MethodArgumentNotValidException){
            MethodArgumentNotValidException exception= (MethodArgumentNotValidException) e;
            //获取自动精简后的异常消息:校验不通过的具体原因
            return exception.getBindingResult().getFieldError().getDefaultMessage();
        }else if(e instanceof EmosException){
            //如果是自定义的异常消息
            EmosException exception= (EmosException) e;
            return exception.getMsg();
        }else if(e instanceof UnauthorizedException){
            //如果是未授权，权限异常
            return "你不具有相关权限";
        }else{
            return "后端执行异常";
        }

    }

}
