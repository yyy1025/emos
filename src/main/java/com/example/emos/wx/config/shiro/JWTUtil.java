package com.example.emos.wx.config.shiro;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateUtil;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@Slf4j
public class JWTUtil {
    @Value("${emos.jwt.secret}")
    private String secret;
    @Value("${emos.jwt.expire}")
    private int expire;
    //传入用户ID，生成token
    public String createToken(int userId){
        //偏移5天之后的过期时间,
        Date date= DateUtil.offset(new Date(), DateField.DAY_OF_YEAR,5);
        //利用加密密钥得到加密算法对象
        Algorithm algorithm=Algorithm.HMAC256(secret);
        //创建JWT内部类对象
        JWTCreator.Builder builder= JWT.create();
        String token=builder.withClaim("userId",userId).withExpiresAt(date).sign(algorithm);
        return token;
    }
    //分解令牌得到用户ID
    public int getUserId(String token){
        //创建一个解码对象
        DecodedJWT jwt = JWT.decode(token);
        //调用对象的解码方法得到用户ID
        int userId=jwt.getClaim("userId").asInt();
        return userId;
    }
    //验证令牌字符串
    public void verifierToken(String token){
        //创建算法对象
        Algorithm algorithm=Algorithm.HMAC256(secret);
        //根据算法对象，创建验证对象
        JWTVerifier jwtVerifier=JWT.require(algorithm).build();
        //验证token,如果内容不对或者令牌过期会自动捕获异常
        jwtVerifier.verify(token);

    }


}
