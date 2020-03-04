package com.jy.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Date;

/**
 * jwt token 工具类
 */
public class JwtTokenUtils {
    /**
     * 实例
     */
    private static JwtTokenUtils instance;

    /**
     * 发行者
     */
    private String subObject = "owner";

    /**
     * 过期时间，默认7天
     */
    private long expired = 1000 * 60 * 60 * 24 * 7;

    /**
     * jwt构造
     */
    private static JwtBuilder jwtBuilder;

    /**
     * 密钥
     */
    private String secret = "secret";// 密钥

    /**
     * 获取实例
     * @return
     */
    public static JwtTokenUtils getInstance(){
        if (instance == null){
            instance = new JwtTokenUtils();
        }
        jwtBuilder = Jwts.builder();
        return instance;
    }

    /**
     * 荷载信息(通常是一个User信息，还包括一些其他的元数据)
     * @param key
     * @param val
     * @return
     */
    public JwtTokenUtils setClaim(String key,Object val){
        jwtBuilder.claim(key,val);
        return this;
    }

    /**
     * 生成 jwt token
     * @return
     */
    public String generateToken(){
        String token = jwtBuilder
                .setSubject(subObject) // 发行者
                //.claim("id","121") // 参数
                .setIssuedAt(new Date()) // 发行时间
                .setExpiration(new Date(System.currentTimeMillis() + expired))
                .signWith(SignatureAlgorithm.HS256,secret) // 签名类型 与 密钥
                .compact(); // 压缩一下
        return token;
    }

    /**
     * 解析 token
     * @param token
     * @return
     */
    public Claims check(String token){
        try{
            final Claims claims = Jwts.parser()
                    .setSigningKey(secret)
                    .parseClaimsJws(token)
                    .getBody();
            return claims;
        }catch (Exception e){}
        return null;
    }

    /**
     * 设置发行者
     * @param subObject
     * @return
     */
    public JwtTokenUtils setSubObject(String subObject) {
        this.subObject = subObject;
        return this;
    }

    public long getExpired() {
        return expired;
    }

    /**
     * 设置过期时间
     * @param expired
     * @return
     */
    public JwtTokenUtils setExpired(long expired) {
        this.expired = expired;
        return this;
    }

    public String getSecret() {
        return secret;
    }

    /**
     * 设置密钥
     * @param secret
     * @return
     */
    public JwtTokenUtils setSecret(String secret) {
        this.secret = secret;
        return this;
    }
}
