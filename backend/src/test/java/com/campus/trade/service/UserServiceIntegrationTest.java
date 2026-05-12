package com.campus.trade.service;

import com.campus.trade.IntegrationTestSupport;
import com.campus.trade.dto.LoginRequest;
import com.campus.trade.dto.RegisterRequest;
import com.campus.trade.dto.UserLoginVO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

class UserServiceIntegrationTest extends IntegrationTestSupport {

    @Autowired
    private UserService userService;

    @Test
    void loginShouldReturnTokenForValidUser() {
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setUsername("login_case_user");
        registerRequest.setPassword("123456");
        registerRequest.setNickname("登录测试用户");
        registerRequest.setPhone("13900000009");
        userService.register(registerRequest);

        LoginRequest request = new LoginRequest();
        request.setUsername("login_case_user");
        request.setPassword("123456");

        UserLoginVO result = userService.login(request);

        assertThat(result).isNotNull();
        assertThat(result.getUsername()).isEqualTo("login_case_user");
        assertThat(result.getRole()).isEqualTo("USER");
        assertThat(result.getToken()).isNotBlank();
    }
}