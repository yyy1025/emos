package com.example.emos.wx.controller;

import cn.hutool.json.JSONUtil;
import com.example.emos.wx.common.utils.R;
import com.example.emos.wx.config.shiro.JWTUtil;
import com.example.emos.wx.controller.form.RegisterForm;
import com.example.emos.wx.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Set;

//编写web层代码，接收客户端数据


@RestController
@RequestMapping("/register")
@Api("注册接口")//api说明
public class UserController {
    @Autowired
    private JWTUtil jwtUtil;
    @Autowired
    private RedisTemplate redisTemplate;
    @Value("${emos.cache-expire}")
    private int cacheExpire;
    private UserService userService;
    //向redis里面缓存token
    public void saveCacheToken(String token,int userId){
        //redis缓存的token，userid【数据库取出的id】，cacheExpire
        redisTemplate.opsForValue().set(token,userId+"",cacheExpire);
    }
    //用户请求
    @PostMapping("/register")
    @ApiOperation("注册用户")//接口说明
    public R register(@Valid@RequestBody RegisterForm registerForm){
        R r=new R();
        //调用userService里面的注册方法，注册用户,返回值是数据库注册用户后的用户id
        Integer id= userService.userRegister(registerForm.getRegisterCode(),registerForm.getCode(),registerForm.getNickName(),registerForm.getPhoto());
        //根据id值：①生成token②获取用户权限③缓存token和用户权限④将token和用户权限封装到R对象，后续返回给客户端
        //根据用户id，生成独一无二的token值
        String token=jwtUtil.createToken(id);
        //获取用户权限
        Set<String>permissions=userService.searchUserPermissions(id);
        //缓存
        saveCacheToken(token,id);
        //封装
        r.ok("用户注册成功");
        r.put("token",token);
        r.put("permissions",permissions);
        return r;


    }

}
