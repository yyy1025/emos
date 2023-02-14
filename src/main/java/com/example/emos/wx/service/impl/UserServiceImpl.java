package com.example.emos.wx.service.impl;

import cn.hutool.db.DaoTemplate;
import cn.hutool.http.HtmlUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.example.emos.wx.db.dao.TbUserDao;
import com.example.emos.wx.exception.EmosException;
import com.example.emos.wx.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.Set;

@Service
@Slf4j
@Scope("prototype")
public class UserServiceImpl implements UserService {
    @Value("${wx.app-id}")
    private String appId;
    @Value("${wx.app-secret}")
    private String appSecret;
    //声明dao的引用
    private TbUserDao tbUserDao;
    //后端换取openid
    private  String getOpenId(String code){
        String url="";
        HashMap map=new HashMap();
        map.put("appid",appId);
        map.put("secret",appSecret);
        map.put("code",code);
        map.put("grant_type","authorization_code");
        //换取openid,得到的是包含openid的json字符串
        String response= HttpUtil.post(url,map);
        //将json字符串转换为json对象，再取出openid
        String openId= JSONUtil.parseObj(response).getStr("openid");
        if(openId==null||openId.length()==0){
            //微信平台url出现问题，抛出RuntimeException异常
            throw new RuntimeException("获取临时登录凭证openid产生错误");
        }
        return openId;
    }
    //根据换取的openid，注册新用户
    //需要传入的参数是激活码、临时授权信息、昵称、头像
//    返回新创建用户的id
    public Integer userRegister(String registerCode,String code,String nickName,String photo){
        //判断是否是超级管理员
        //但是激活码为000000的一定是想创建超级管路员吗
        if(registerCode.equals("000000")){
            //是否root字段有值
            if(! tbUserDao.havaRooterUser()){
                //还没有超级管理员，就插入超级管理员数据
                String openId=getOpenId(code);
                HashMap map = new HashMap();
                map.put("openId",openId);
                map.put("nickNamw",nickName);
                map.put("photo",photo);
                map.put("role","[0]");//超级管理员权限[0]
                map.put("status",1);//正常在职状态
                map.put("createTime",new Date());
                map.put("root",true);
                //插入数据到数据库
                tbUserDao.insert(map);
                //返回新创建用户的id
                Integer id=tbUserDao.searchIdByOpenid(openId);
                return id;

            }
            else{
                //业务出现问题，抛出EmosException异常
                throw new EmosException("超级管理员账户已经被绑定，无权创建");
            }
        }else{
            //TODO 普通员工的注册
            return 1;
        }
    }
    public Set<String> searchUserPermissions(int userId){
        return tbUserDao.searchUserPermissions(userId);
    }

}
