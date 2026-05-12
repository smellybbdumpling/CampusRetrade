package com.campus.trade.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class GoodsMessageVO {

    private Long id;

    private Long goodsId;

    private Long senderId;

    private String senderName;

    private String senderUsername;

    private Long parentId;

    private String content;

    private LocalDateTime createTime;

    private List<GoodsMessageVO> replies;
}