package com.campus.trade.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserVO {

    private Long id;

    private String username;

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

    private Long publishedGoodsCount;

    private Long approvedGoodsCount;

    private Long finishedBuyOrderCount;

    private Long finishedSellOrderCount;

    private Long totalReportCount;

    private Long effectiveReportCount;

    private Long penaltyCount;

    private Integer creditScore;

    private String creditLevel;

    private LocalDateTime createTime;
}