package com.example.emos.wx.aop;

import com.example.emos.wx.common.utils.R;
import com.example.emos.wx.config.shiro.ThreadLocalToken;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.annotation.PersistenceCreator;
import org.springframework.stereotype.Component;

//创建AOP切面类，把更新后的令牌返回给客户端
//拦截web方法返回值，向其中添加新token
//返回值是我们封装的R对象类型
//需要先判断是否需要更新token，再向返回值中添加
@Component
@Aspect
public class TokenAspect {
    @Autowired
    private ThreadLocalToken threadLocalToken;
    @Pointcut("execution(public * com.example.emos.wx.controller.*.*(..))")
    public void aspect(){

    }
    @Around("aspect()")//给切点加上通知事件
    public Object round(ProceedingJoinPoint point) throws Throwable {
        //获取web方法返回值
        R r=(R)point.proceed();
        //检查是否新生成了令牌
        String token=threadLocalToken.getToken();
        if(token!=null){
            //token不为空，说明需要更新令牌
            r.put("token",token);
            //清除threadLocalToken里面的token值
            threadLocalToken.clearToken();
        }
        return r;

    }

}
