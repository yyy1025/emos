package com.example.emos.wx.config;

import io.swagger.annotations.ApiOperation;
//import io.swagger.v3.oas.models.security.SecurityScheme;
//import io.swagger.annotations.AuthorizationScope;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
//注意包导入不要出错
import springfox.documentation.service.AuthorizationScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.AuthorizationScopeBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.*;
import springfox.documentation.service.SecurityScheme;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;
import springfox.documentation.service.ApiInfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
//利用swagger测试接口
//需要添加依赖，swagger3+springboot，注意版本的对应
//SwaggerConfig配置swagger

@Configuration//这个注解是启动的时候加载这个类
@EnableSwagger2//表示这个项目加载SwaggerAPI文档
public class SwaggerConfig {
    //    apiInfo方法-ApiInfoBuilder主要配置页面基本显示信息

    private ApiInfo apiInfo(){
        return new ApiInfoBuilder()
                .title("swagger-api文档:ans在线答疑系统")
                .description("swagger文档by 姚燕燕")
                .version("1.0")
                .build();
    }
    //返回一个Docket实例，指定控制器包的路径，在路径下的controller类才会被扫描，如果有与swagger相关的注解，才会自动生成swaggerapi文档，
    //ApiSelectorBuilder规定哪些类中的哪些方法可以出现在swagger中
    @Bean
public Docket createRestApi(){
        return new Docket(DocumentationType.OAS_30)
                .apiInfo(apiInfo())
                .select()//创建APISelectBuilder对象，ApiSelectorBuilder asb=docket.select();
                .apis(RequestHandlerSelectors.basePackage(""))
                .paths(PathSelectors.any())//选定Controller类的范围为所有包下的所有类
                .apis(RequestHandlerSelectors.withMethodAnnotation(ApiOperation.class))//限定使用这个注解的才可以被识别
                .build()
//                .securitySchemes(securitySchemesList);
                .securitySchemes(Collections.singletonList(securityScheme()))
                .securityContexts(Collections.singletonList(securityContext()));
        //
    }

    private SecurityScheme securityScheme() {
        return new ApiKey("token", "token", "header");
    }
    private SecurityContext securityContext() {
        return SecurityContext.builder()
                .securityReferences(defaultAuth())
                .forPaths(PathSelectors.regex("/api/.*"))
                .build();
    }
    List<SecurityReference> defaultAuth() {
        AuthorizationScope authorizationScope =  new AuthorizationScopeBuilder().scope("global").description("access everything").build();;
        AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
        authorizationScopes[0] = authorizationScope;
        return Collections.singletonList(new SecurityReference("JWT", authorizationScopes));
    }

    //开启对JWT的支持，实现一个单点登录的功能，只有登录后才能够使用的功能。
}



