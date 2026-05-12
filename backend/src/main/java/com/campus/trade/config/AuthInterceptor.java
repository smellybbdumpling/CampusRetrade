package com.campus.trade.config;

import com.campus.trade.common.BusinessException;
import com.campus.trade.common.CurrentUser;
import com.campus.trade.common.JwtUtil;
import com.campus.trade.common.UserContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class AuthInterceptor implements HandlerInterceptor {

    private static final Set<String> PUBLIC_PATHS = Set.of(
            "/api/categories",
            "/api/goods/page"
    );

    private final JwtUtil jwtUtil;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (isPublicRequest(request)) {
            String authorizationHeader = request.getHeader("Authorization");
            if (StringUtils.hasText(authorizationHeader) && authorizationHeader.startsWith("Bearer ")) {
                try {
                    CurrentUser currentUser = jwtUtil.parseToken(authorizationHeader.substring(7));
                    UserContext.setCurrentUser(currentUser);
                } catch (Exception ignored) {
                }
            }
            return true;
        }
        String authorization = request.getHeader("Authorization");
        if (!StringUtils.hasText(authorization) || !authorization.startsWith("Bearer ")) {
            throw new BusinessException("未登录或登录已过期");
        }
        String token = authorization.substring(7);
        CurrentUser currentUser;
        try {
            currentUser = jwtUtil.parseToken(token);
        } catch (Exception exception) {
            throw new BusinessException("登录令牌无效或已过期");
        }
        UserContext.setCurrentUser(currentUser);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        UserContext.clear();
    }

    private boolean isPublicRequest(HttpServletRequest request) {
        String requestUri = request.getRequestURI();
        if (PUBLIC_PATHS.contains(requestUri)) {
            return true;
        }
        return "GET".equalsIgnoreCase(request.getMethod())
                && requestUri.startsWith("/api/goods/")
                && !requestUri.startsWith("/api/goods/admin/")
            && !requestUri.startsWith("/api/goods/my");
    }
}