package com.jy.controller;


import com.jy.config.WeChatConfig;
import com.jy.pojo.User;
import com.jy.service.UserService;
import com.jy.utils.JwtTokenUtils;
import com.jy.utils.Response;
import com.jy.utils.WXPayUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;

@RestController
@RequestMapping("/api/v1/wechat")
public class WeChatController {

    private static Logger logger = LoggerFactory.getLogger(WeChatController.class);

    @Autowired
    private WeChatConfig weChatConfig;
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private UserService userService;

    /**
     * 微信扫描登陆 url， 注意：地址需要有 http://，否则无法跳转
     * @param currentPage  记录当前请求页面地址，用于登陆成功后跳转地址（注意：地址需要有 http://，否则无法跳转）
     * @return
     */
    @GetMapping("/login_url")
    public Response loginUrl(@RequestParam("current_page")String currentPage) throws UnsupportedEncodingException {
        // 对回调地址进行编码
        String callbackUrl = URLEncoder.encode(weChatConfig.getOpenRedirectUrl(),"GBK");
        String qrcodeUrl = String.format(weChatConfig.getOPEN_QRCODE_URL(),weChatConfig.getOpenAppid(),callbackUrl,currentPage);
        Map map = new HashMap();
        map.put("qrcode_url",qrcodeUrl);
        return Response.success(map);
    }

    /**
     * 微信扫描登陆回调方法
     * @param code
     * @param state
     * @return
     */
    @GetMapping("/user/callback")
    public void callback(@RequestParam("code")String code, String state, HttpServletResponse response) throws IOException {
        String accessTokenUrl = String.format(weChatConfig.getOPEN_ACCESS_TOKEN_URL(),weChatConfig.getOpenAppid(),weChatConfig.getOpenAppsecret(),code);
        Map<String,Object> result = restTemplate.getForObject(accessTokenUrl, Map.class);
        if (result != null && result.get("access_token") != null && result.get("openid") != null){
            String accessToken = (String) result.get("access_token");
            String openid = (String) result.get("openid");
            // 判断是否是第一次登陆
            User userByOpenid = userService.findByOpenid(openid);
            if (userByOpenid == null){
                // 获取用户基本信息
                String userInfoUrl = String.format(weChatConfig.getOPEN_USER_INFO_URL(),accessToken,openid);
                Map<String,Object> userInfo = restTemplate.getForObject(userInfoUrl, Map.class);
                if (userInfo != null){
                    // 中文乱码处理
                    String nickname = (String) userInfo.get("nickname");
                    nickname = new String(nickname.getBytes("ISO-8859-1"),"UTF-8");
                    Integer sex = (Integer) userInfo.get("sex");
                    String headimgurl = (String) userInfo.get("headimgurl");
                    String country = (String) userInfo.get("country");// 国家
                    String province = (String) userInfo.get("province");// 省
                    String city = (String) userInfo.get("city");// 城市
                    String address = new String((country + province + city).getBytes("ISO-8859-1"),"UTF-8");
                    User user = User.builder().name(nickname).openid(openid).headImg(headimgurl).city(address).sex(sex).build();
                    user.setSign("微信资料个人签名");
                    user.setCreateTime(new Date());
                    User user1 = userService.save(user);
                    userByOpenid = user1;
                }
            }
            //==============  保存成功，生成 token 重定向到 传递的 state 页面 ==============
            String token = JwtTokenUtils.getInstance()
                    .setClaim("id",userByOpenid.getId())
                    .setClaim("head_img",userByOpenid.getHeadImg())
                    .setClaim("name",userByOpenid.getName())
                    .generateToken();
            // 注意： 当前跳转地址需要前端传递的 页面地址拼接上 http:// ,否则无法跳转
            response.sendRedirect(state + "?token=" + token + "&head_img=" + userByOpenid.getHeadImg() + "&name=" + userByOpenid.getName());
        }else{
            logger.error("【微信登陆回调获取 access_token 异常】，{}",result);
        }
    }

    /**
     * 微信支付回调（post方式）
     */
    @RequestMapping("/order/callback")
    public String orderCallback(HttpServletRequest request,HttpServletResponse response) throws Exception {
        // 获取微信回调参数
        InputStream inputStream= request.getInputStream();// 读取字节流
        InputStreamReader reader = new InputStreamReader(inputStream, "UTF-8");// 字节流转字符流
        BufferedReader in = new BufferedReader(reader);// 吧字符流放到字符缓冲流中读取，比 InputStreamReader 更快
        StringBuffer buffer = new StringBuffer();
        String line;
        while ((line = in.readLine()) != null){
            buffer.append(line);
        }
        // 关闭流
        in.close();
        inputStream.close();
        Map<String, String> params = WXPayUtil.xmlToMap(buffer.toString());
        // 商户密钥
        String key = "xxxxxxxxx";
        // 进行参数字典排序
        SortedMap<String, String> sortedMap = WXPayUtil.coverMap2SortedMap(params);
        // 验签
        if (WXPayUtil.checkSign(sortedMap,key,"HMAC-SHA256")){
            if("SUCCESS".equals(sortedMap.get("result_code"))){
                // 验证通过
                // 业务逻辑，如：更新订单状态
                //通知微信订单处理成功
                response.setContentType("text/xml");
                /*
                response.getWriter().println("fail");
                response.getWriter().println(WXPayUtil.returnXML("SUCCESS"));
                return;
                */
                return WXPayUtil.returnXML("SUCCESS");
            }
        }else{
            System.out.println("验签失败！");
            //都处理失败
            /*response.setContentType("text/xml");
            response.getWriter().println("fail");*/
        }
        return WXPayUtil.returnXML("FAIL");
    }
}
