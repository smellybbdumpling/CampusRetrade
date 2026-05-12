package com.campus.trade.dto;

import lombok.Data;

@Data
public class AdminUserQueryDTO {

    private Integer pageNum = 1;

    private Integer pageSize = 10;

    private String username;

    private String role;

    private String status;
}