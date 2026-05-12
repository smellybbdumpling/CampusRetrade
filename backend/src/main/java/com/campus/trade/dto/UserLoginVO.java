package com.campus.trade.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserLoginVO {

    private Long userId;

    private String username;

    private String nickname;

    private String role;

    private String token;
}