package com.jy.config;


import com.jy.interceptor.LoginInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    /**
     * 跨域处理
     * @param registry
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*")
                .allowedMethods("GET","POST","PUT","DELETE","OPTIONS") // 真实请求允许的方法
                .allowedHeaders("Origin", "X-Requested-With", "Content-Type", "Accept", "token") // 服务器允许使用的字段
                .allowCredentials(true) // 是否允许用户发送、处理 cookie
                .maxAge(3600); // 预检请求的有效期，单位为秒。有效期内，不会重复发送预检请求
    }

    /**
     * 拦截器配置
     * @param registry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LoginInterceptor())
                .addPathPatterns("/user/api/v1/*/**");

        WebMvcConfigurer.super.addInterceptors(registry);
    }
}
