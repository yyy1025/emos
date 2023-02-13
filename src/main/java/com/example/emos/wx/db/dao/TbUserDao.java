package com.example.emos.wx.db.dao;

import com.example.emos.wx.db.pojo.TbUser;
import org.apache.ibatis.annotations.Mapper;

import java.util.HashMap;

@Mapper
public interface TbUserDao {
    //dao是接口类
    public boolean havaRooterUser();
    public int insert(HashMap param);
    public Integer searchIdByOpenid(String openId);

}