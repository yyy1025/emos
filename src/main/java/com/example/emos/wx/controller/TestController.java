package com.example.emos.wx.controller;

import com.example.emos.wx.common.utils.R;
import com.example.emos.wx.form.TestSayHelloForm;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/test")
@Api("测试web接口")//api说明
public class TestController {
    @ApiOperation("最简单的测试方法，加上这个注解才可以被识别哦~")
//    @GetMapping("/hello")
//    public R sayhello(){
//        return new R().ok().put("msg","hello yyy-syc");
//    }
    @PostMapping("/hello")
    public R sayhello(@Valid@RequestBody TestSayHelloForm testSayHelloForm){
        return new R().ok().put("msg","hello yyy-syc"+testSayHelloForm.getName());
    }
}
