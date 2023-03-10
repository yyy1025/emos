package com.example.emos.wx.config.shiro;

import org.springframework.stereotype.Component;
//创建存储令牌的媒介类
//临时保存token
@Component
public class ThreadLocalToken {
    ThreadLocal<String>local=new ThreadLocal<>();
    public void setToken(String token){
        local.set(token);
    }
    public String getToken(){
        return local.get();
//        return null;
    }
    public void clearToken(){
        local.remove();
    }

}
