package com.jy.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jy.utils.JwtTokenUtils;
import com.jy.utils.Response;
import io.jsonwebtoken.Claims;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class LoginInterceptor implements HandlerInterceptor {

    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = request.getHeader("token");
        if (token != null){
            // 尝试从参数获取 token，特殊场景下需要将 token 放到 参数里
            token = request.getParameter("token");
        }
        if (token != null){
            Claims claims = JwtTokenUtils.getInstance().check(token);
            if (claims != null){
                String id = (String) claims.get("id");
                String name = (String) claims.get("name");
                if (id != null && name != null){
                    // 验证通过，设置参数
                    request.setAttribute("user_id",id);
                    request.setAttribute("name",name);
                    return true;
                }
            }
        }
        responseJson(response,"请先登陆");
        return false;
    }

    /**
     * 返回 json 格式数据
     */
    private void responseJson(HttpServletResponse response,String message) throws IOException {
        response.setContentType("application/json;charset=utf-8");
        PrintWriter writer = response.getWriter();
        writer.write(objectMapper.writeValueAsString(Response.error(message)));
        writer.close();
        response.flushBuffer();
    }
}
