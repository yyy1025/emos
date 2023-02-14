package com.example.emos.wx.controller.form;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

//封装表单类，接收从客户端提交过来的数据
@Data
@ApiModel
public class RegisterForm {
    //激活码不为空，校验规则为六位数字
    @NotBlank(message = "用户邀请码不能为空")
    @Pattern(regexp = "^[0-9]{6}$",message = "邀请码必须为6位数字")
    private String registerCode;
    //用户授权信息不为空
    @NotBlank(message = "用户授权信息不能为空")
    private String code;
    //
    @NotBlank(message = "用户昵称不能为空")
    private String nickName;
    //
    @NotBlank(message = "用户头像不能为空")
    private String photo;
}
