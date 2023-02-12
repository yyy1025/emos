package com.example.emos.wx.config.xss;


import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
//拦截请求的路径:设置为所有的路径
@WebFilter(urlPatterns = "/*")
public class XssFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        Filter.super.init(filterConfig);
    }
    //实现doFilter方法，拦截请求
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        //得到装饰器模式包装过的请求对象
        XssHttpServletRequestWrapper wrapper=new XssHttpServletRequestWrapper((HttpServletRequest) servletRequest);
        //拦截请求
        filterChain.doFilter(wrapper,servletResponse);

    }

    @Override
    public void destroy() {
        Filter.super.destroy();
    }
}
