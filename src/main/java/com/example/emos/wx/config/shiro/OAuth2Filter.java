package com.example.emos.wx.config.shiro;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpStatus;
import cn.hutool.http.server.HttpServerResponse;
import com.auth0.jwt.exceptions.JWTDecodeException;
//import io.swagger.annotations.Scope;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.web.filter.authc.AuthenticatingFilter;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMethod;
import org.thymeleaf.exceptions.TemplateEngineException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@Scope("prototype")
//有了这个注解意味着每次请求的时候都创建bean实例，意味着将创建同一个bean的多个实例
public class OAuth2Filter extends AuthenticatingFilter {
    private ThreadLocalToken threadLocalToken;
    @Value("${emos.jwt.expire}")
    private int expire;
    @Value("${emos.jwt.cache-expire}")
    private int cacheExpire;
    @Autowired
    private  JWTUtil jwtUtil;
//    @Autowired
//    private RabbitTemplate rabbitTemplate;
    @Autowired
    private RedisTemplate redisTemplate;
    //从请求头获取token
    public String getRequestToken(HttpServletRequest httpServletRequest){
        String token=httpServletRequest.getHeader("token");
        if(StrUtil.isBlank(token)){
            token=httpServletRequest.getParameter("token");
        }
        return token;

    }
    //封装令牌字符串
    @Override
    protected AuthenticationToken createToken(ServletRequest servletRequest, ServletResponse servletResponse) throws Exception {
//        return null;
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        String token=getRequestToken(request);
        if(StrUtil.isBlank(token)){
            return null;
        }
        return new OAuth2Token(token);
    }

    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) {
        HttpServletRequest req= (HttpServletRequest) request;
        //如果是options请求就
        if(req.getMethod().equals(RequestMethod.OPTIONS.name())){
            //是options请求就被放行
            return true;
        }
        //不是options请求需要被shiro框架处理
        return false;
    }
    //被shiro框架处理的请求到这里
    @Override
    protected boolean onAccessDenied(ServletRequest servletRequest, ServletResponse servletResponse) throws Exception {

        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response= (HttpServletResponse) servletResponse;
        //设置响应头
        response.setContentType("text/html");
        response.setCharacterEncoding("UTF-8");
        //允许跨域设置
        response.setHeader("Access-Control-Allow-Credentials","true");
        response.setHeader("Access-Control-Allow-Origin",request.getHeader("Origin"));
        //清空threadLocal
        threadLocalToken.clearToken();
        //获取token，根据token进行判断
        String token=getRequestToken(request);
        //token为空
        if(StrUtil.isBlank(token)){
            response.setStatus(HttpStatus.HTTP_UNAUTHORIZED);
            response.getWriter().println("无效的令牌");
            return false;
        }
        //验证token内容是否有效，时间是否过期
        try{
            jwtUtil.verifierToken(token);
        }catch(TemplateEngineException e){
            //捕获过期异常
            //redis保存的令牌没有过期，但是客户端令牌过期，需要令牌刷新
            //查看redis缓存的token
            //将token这个字符串查询
            if(redisTemplate.hasKey(token)){
                redisTemplate.delete(token);
                //从已经保存的令牌中获取用户ID
                int userId= jwtUtil.getUserId(token);
                //生成新的令牌
                token=jwtUtil.createToken(userId);
                //新生成的令牌缓存在redis里面,缓存内容为token值、userid、过期时间cacheExpire
                redisTemplate.opsForValue().set(token,userId+"",cacheExpire);
                //将新生成的令牌缓存到threadLocalToken中
                threadLocalToken.setToken(token);
            }else{
                //缓存令牌也过期，需要重新登录，返回错误信息：令牌已经过期
                response.setStatus(HttpStatus.HTTP_UNAUTHORIZED);
                response.getWriter().println("令牌已过期");

            }


        }catch(JWTDecodeException e){
            //捕获内容异常
            response.setStatus(HttpStatus.HTTP_UNAUTHORIZED);
            response.getWriter().println("无效令牌");
        }
        //执行这个方法可以让shiro间接地执行认证授权
        boolean res=executeLogin(request,response);
        return res;


    }
    //认证失败进行这个处理，授权失败在上面onAccessDenied中已经处理
    //如果没有登录，也就是未认证的话，在这里返回异常信息
    @Override
    protected boolean onLoginFailure(AuthenticationToken token, AuthenticationException e, ServletRequest servletRequest, ServletResponse servletResponse) {
        HttpServletRequest request= (HttpServletRequest) servletRequest;
        HttpServletResponse response= (HttpServletResponse) servletResponse;
        //设置响应头
        response.setContentType("text/html");
        response.setCharacterEncoding("UTF-8");
        //允许跨域设置
        response.setHeader("Access-Control-Allow-Credentials","true");
        response.setHeader("Access-Control-Allow-Origin",request.getHeader("Origin"));
        response.setStatus(HttpStatus.HTTP_UNAUTHORIZED);
        try {
            response.getWriter().println(e.getMessage());

        }catch (IOException ex) {
            throw new RuntimeException(ex);
        }catch(Exception exception){

        }
        return false;
    }
    //Filter类必包含的doFilter方法

    @Override
    public void doFilterInternal(ServletRequest request, ServletResponse response, FilterChain chain) throws ServletException, IOException {
        super.doFilterInternal(request, response, chain);
    }
}
