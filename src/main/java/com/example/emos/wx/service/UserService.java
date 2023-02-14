package com.example.emos.wx.service;

import java.util.Set;

//定义UserService接口
public interface UserService {
    //业务层代码：定义接口
    //传入邀请码、授权信息、昵称、头像，进行注册
    public Integer userRegister(String registerCode,String code,String nickName,String photo);
    //根据userid查询对应的权限列表
    public Set<String> searchUserPermissions(int userId);
}
