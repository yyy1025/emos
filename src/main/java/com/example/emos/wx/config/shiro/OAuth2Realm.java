package com.example.emos.wx.config.shiro;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.stereotype.Component;

@Component
public class OAuth2Realm extends AuthorizingRealm {
    private JWTUtil jwtUtil;//是需要用到验证令牌字符串的方法吗
    @Override
    public boolean supports(AuthenticationToken token) {
        //判断传入的token是否是我们封装好的token类的格式
//        return super.supports(token);
        return token instanceof OAuth2Token;
    }
    //授权:验证权限的时候
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        SimpleAuthorizationInfo info=new SimpleAuthorizationInfo();
        //TODO 查用户的权限列表
        //TODO 添加权限列表到info对象里面
        return info;
//        return null;
    }

    //认证：登录的时候调用
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        //TODO 从令牌里面取出userid，检测账户是否被冻结
        SimpleAuthenticationInfo info=new SimpleAuthenticationInfo();
        //TODO 往info对象里面添加用户信息，TOKEN字符串
        return info;
//        return null;
    }
}
