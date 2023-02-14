package com.example.emos.wx.db.dao;

import com.example.emos.wx.db.pojo.TbUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.shiro.web.tags.UserTag;

import java.util.HashMap;
import java.util.Set;

@Mapper
public interface TbUserDao {
    //dao是接口类
    public boolean havaRooterUser();
    public int insert(HashMap param);
    public Integer searchIdByOpenid(String openId);
//    根传入用户id查询该用户对应的权限列表，返回类型是set<String>
    public Set<String> searchUserPermissions(int userId);

}
