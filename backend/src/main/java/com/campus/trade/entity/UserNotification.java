package com.campus.trade.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.campus.trade.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("user_notification")
public class UserNotification extends BaseEntity {

    private Long userId;

    private String type;

    private String title;

    private String content;

    private String actionUrl;

    private Boolean readFlag;
}