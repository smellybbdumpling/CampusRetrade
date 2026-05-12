package com.campus.trade.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserNotificationVO {

    private Long id;

    private String type;

    private String title;

    private String content;

    private String actionUrl;

    private Boolean readFlag;

    private LocalDateTime createTime;
}