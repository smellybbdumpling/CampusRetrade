package com.campus.trade.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserProfileUpdateRequest {

    @NotBlank(message = "昵称不能为空")
    @Size(max = 50, message = "昵称不能超过50个字符")
    private String nickname;

    @Pattern(regexp = "^$|^1\\d{10}$", message = "手机号必须为11位大陆手机号")
    private String phone;

    @Pattern(regexp = "^$|^(MALE|FEMALE|OTHER)$", message = "性别参数不正确")
    private String gender;

    @Size(max = 500, message = "个人简介不能超过500个字符")
    private String bio;

    @Size(max = 100, message = "学校不能超过100个字符")
    private String school;

    @Size(max = 100, message = "学院不能超过100个字符")
    private String college;

    @Size(max = 100, message = "专业不能超过100个字符")
    private String major;

    @Size(max = 50, message = "年级不能超过50个字符")
    private String grade;

    @Size(max = 50, message = "微信号不能超过50个字符")
    @Pattern(regexp = "^$|^[a-zA-Z][-_a-zA-Z0-9]{5,19}$", message = "微信号格式不正确")
    private String wechat;

    @Pattern(regexp = "^$|^[1-9]\\d{4,11}$", message = "QQ号格式不正确")
    private String qq;

    @Email(message = "邮箱格式不正确")
    @Size(max = 100, message = "邮箱不能超过100个字符")
    private String email;

    @Size(max = 100, message = "常用交易地点不能超过100个字符")
    private String tradeLocation;

    @Size(max = 100, message = "支持交易方式不能超过100个字符")
    private String tradeMethods;

    private Boolean acceptBargain;

    @Size(max = 100, message = "常在线时间不能超过100个字符")
    private String onlineTime;

    private Boolean phonePublic;

    private Boolean wechatPublic;
}