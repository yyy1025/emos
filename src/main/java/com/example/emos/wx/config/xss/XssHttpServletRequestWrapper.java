package com.example.emos.wx.config.xss;




import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HtmlUtil;
import cn.hutool.http.server.HttpServerRequest;
import cn.hutool.json.JSONUtil;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.support.HttpRequestWrapper;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.*;
import java.nio.charset.Charset;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Map;
//抵御跨站脚本攻击
//利用Hutool里面的方法，对客户端发送的数据进行转义
//怎么对数据做转义呢？一种方法就是编写过滤器，拦截客户端请求，对于请求数据做转义；
//覆盖HTTP方法，比如getParameter等，对获取的数据都进行转义
public class XssHttpServletRequestWrapper extends HttpServletRequestWrapper {
    public XssHttpServletRequestWrapper(HttpServletRequest request){
        super(request);
    }

    @Override
    public String getParameter(String name) {
        String value=super.getParameter(name);
        //判断数据有效，不等于空，不等于空值，StrUtil.hasEmpty
        if(!StrUtil.hasEmpty(value)){
            //利用HtmlUtil中的filter方法对数据进行转义
            value=HtmlUtil.filter(value);

        }
        return value;
    }

    @Override
    public String[] getParameterValues(String name) {
        String[]values=super.getParameterValues(name);
        //数组不为null
        if(values!=null){
            for(int i=0;i<values.length;i++){
                String value=values[i];
                if(!StrUtil.hasEmpty(value)){
                    //利用HtmlUtil中的filter方法对数据进行转义
                    value=HtmlUtil.filter(value);
                }
                //必须要在if外面赋值，不然if不生效就惨了
                values[i]=value;

            }

        }
        return values;
    }

    @Override
    public Map<String, String[]> getParameterMap() {
        Map<String, String[]> parameters=super.getParameterMap();
        //使用LinkedHashMap数据类型，保证插入的顺序
        LinkedHashMap<String, String[]>linkedHashMap=new LinkedHashMap<>();
        if(parameters!=null){
            //遍历map数据类型的key值，用keySet方法
            for(String key:parameters.keySet()){
                //通过key获取values数组
                String[]values=parameters.get(key);
                if(values!=null){
                    for(int i=0;i<values.length;i++){
                        String value=values[i];
                        if(!StrUtil.hasEmpty(value)){
                            //利用HtmlUtil中的filter方法对数据进行转义
                            value=HtmlUtil.filter(value);
                        }
                        //必须要在if外面赋值，不然if不生效就惨了
                        values[i]=value;
                    }

                }
                linkedHashMap.put(key,values);
            }
        }

        return linkedHashMap;
    }

    @Override
    public String getHeader(String name) {
        String value=super.getHeader(name);
        //判断数据有效，不等于空，不等于空值，StrUtil.hasEmpty
        if(!StrUtil.hasEmpty(value)){
            //利用HtmlUtil中的filter方法对数据进行转义
            value=HtmlUtil.filter(value);
        }
        return value;

    }

   //最重要的一个方法，因为springmvc就是利用这个方法

    @Override
    public ServletInputStream getInputStream() throws IOException {
        InputStream inputStream = super.getInputStream();
        InputStreamReader inputStreamReader=new InputStreamReader(inputStream,Charset.forName("UTF-8"));
        //为了读取高效性
        BufferedReader bufferedReader=new BufferedReader(inputStreamReader);
        //把从缓冲流中读取的数据进行保存到stringBuffer容器中
        StringBuffer stringBuffer=new StringBuffer();
        //一行一行地读取
        String line=bufferedReader.readLine();
        while(line!=null){
            //如果有值，就拼接到容器中
            stringBuffer.append(line);
            line=bufferedReader.readLine();
        }
        //关闭流
        bufferedReader.close();
        inputStreamReader.close();
        inputStream.close();
        //对读取的数据进行转义，是json格式的字符串
        //自动把json格式字符串转换成map类型，JSONUtil.parseObj
        Map<String,Object>map = JSONUtil.parseObj(stringBuffer.toString());
        Map<String,Object>res=new LinkedHashMap<>();
        for(String key:map.keySet()){
            //遍历key，得到value，看Object类型的数据是否可以转换成字符串，如果可以的话就转义，不能的话就直接存储到res里面
            Object value=map.get(key);
            if(value instanceof  String){

                if(!StrUtil.hasEmpty(value.toString())){
                    //利用HtmlUtil中的filter方法对数据进行转义
                    value=HtmlUtil.filter(value.toString());
                }
                //必须要在if外面赋值，不然if不生效就惨了


//            }else{
//                res.put(key,value);
            }
            res.put(key,value);

        }
        //返回一个IO流
        //先把这个map转回json格式字符串
        String jsons=JSONUtil.toJsonStr(res);
        //
        ByteArrayInputStream byteArrayInputStream=new ByteArrayInputStream(jsons.getBytes());
        return new ServletInputStream() {
            @Override
            public boolean isFinished() {
                return false;
            }

            @Override
            public boolean isReady() {
                return false;
            }

            @Override
            public void setReadListener(ReadListener readListener) {

            }

            @Override
            public int read() throws IOException {
                return byteArrayInputStream.read();
            }
        };
    }
}
