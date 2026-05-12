package com.campus.trade.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.campus.trade.IntegrationTestSupport;
import com.campus.trade.common.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

@AutoConfigureMockMvc
public abstract class ControllerTestSupport extends IntegrationTestSupport {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected JwtUtil jwtUtil;

    protected String bearerTokenForAdmin() {
        return "Bearer " + jwtUtil.generateToken(adminUser.getId(), adminUser.getUsername(), adminUser.getRole());
    }

    protected String bearerTokenForSeller() {
        return "Bearer " + jwtUtil.generateToken(sellerUser.getId(), sellerUser.getUsername(), sellerUser.getRole());
    }

    protected String bearerTokenForBuyer() {
        return "Bearer " + jwtUtil.generateToken(buyerUser.getId(), buyerUser.getUsername(), buyerUser.getRole());
    }
}