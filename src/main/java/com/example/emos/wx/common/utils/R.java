package com.example.emos.wx.common.utils;

import org.apache.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;
//封装web返回对象
//对后端处理后的数据进行封装，以一个统一格式返回给客户端;返回的内容是业务状态码，业务消息，业务数据；
//封装后的主要字符，{状态码，消息}{code，msg}
//返回对象类型HashMap<String,Object>
//使用了HttpComponents库，定义了很多的状态码常量，就不需要我们自定义状态码常量了
public class R extends HashMap<String,Object> {
    public R(){
        put("code", HttpStatus.SC_OK);
        put("msg","success");
    }
    //ok方法和error方法共用
    public R put(String key,Object value){
        super.put(key,value);
        return this;
    }
    //ok（）方法生成R对象实例
    public static R ok(){
        return new R();
    }
    public static R ok(int code,String msg){
        R r=new R();
        r.put("code",code);
        r.put("msg",msg);
        return r;
    }
    public static R ok(String msg){
        R r=new R();
        r.put("msg",msg);
        return r;
    }
    //参数HttpStatus.SC_OK
    public static R ok(int code){
        R r=new R();
        r.put("code",code);
        return r;
    }
    public static R ok(Map<String,Object> map){
        R r=new R();
        r.putAll(map);
        return r;
    }

    //失败的返回

    public static R error(int code,String msg){
        R r=new R();
        r.put("code",code);
        r.put("msg",msg);
        return r;
    }
    public static R error(String msg){
        return error(HttpStatus.SC_INTERNAL_SERVER_ERROR,msg);
    }
    public static R error(){
        return error(HttpStatus.SC_INTERNAL_SERVER_ERROR,"未知异常，请联系管理员");
    }
}


