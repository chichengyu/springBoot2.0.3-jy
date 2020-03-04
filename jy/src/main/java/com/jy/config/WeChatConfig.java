package com.jy.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;


@Data
@Configuration
//@PropertySource(value = "classpath:application-dev.yml")
//@ConfigurationProperties(prefix = "wxpay")
public class WeChatConfig {

    // ============================== 微信公众平台配置 ==============================
    /**
     * 公众号appid
     */
    @Value("${wxpay.appid}")
    private String appid;

    /**
     * 公众号秘钥
     */
    @Value("${wxpay.appsecret}")
    private String appsecret;

    /**
     * 商户号id
     */
    @Value("${wxpay.mer_id}")
    private String mchId;

    /**
     * 支付key
     */
    @Value("${wxpay.key}")
    private String key;

    /**
     * 微信支付回调url
     */
    @Value("${wxpay.callback}")
    private String payCallbackUrl;

    // ============================== 微信开放平台配置（扫码登陆） ==============================
    /**
     * 微信开放平台二维码连接
     */
    private String OPEN_QRCODE_URL= "https://open.weixin.qq.com/connect/qrconnect?appid=%s&redirect_uri=%s&response_type=code&scope=snsapi_login&state=%s#wechat_redirect";

    /**
     * 开放平台获取access_token地址
     */
    private String OPEN_ACCESS_TOKEN_URL="https://api.weixin.qq.com/sns/oauth2/access_token?appid=%s&secret=%s&code=%s&grant_type=authorization_code";

    /**
     * 获取用户信息
     */
    private String OPEN_USER_INFO_URL ="https://api.weixin.qq.com/sns/userinfo?access_token=%s&openid=%s&lang=zh_CN";

    /**
     * 开放平台appid
     */
    @Value("${wxopen.appid}")
    private String openAppid;

    /**
     * 开放平台appsecret
     */
    @Value("${wxopen.appsecret}")
    private String openAppsecret;


    /**
     * 开放平台回调url
     */
    @Value("${wxopen.redirect_url}")
    private String openRedirectUrl;
}
