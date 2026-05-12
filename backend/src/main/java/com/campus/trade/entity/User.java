package com.campus.trade.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.campus.trade.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_user")
public class User extends BaseEntity {

    private String username;

    private String password;

    private String nickname;

    private String avatar;

    private String phone;

    private String gender;

    private String bio;

    private String school;

    private String college;

    private String major;

    private String grade;

    private String wechat;

    private String qq;

    private String email;

    private String tradeLocation;

    private String tradeMethods;

    private Boolean acceptBargain;

    private String onlineTime;

    private Boolean phonePublic;

    private Boolean wechatPublic;

    private String role;

    private String status;
}