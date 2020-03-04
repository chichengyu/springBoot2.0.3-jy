package com.jy.controller;

import com.jy.utils.OrderUtil;
import com.jy.utils.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.net.InetAddress;

@RestController
@RequestMapping("/test")
public class Test {

    @Autowired
    private RestTemplate restTemplate;

    @GetMapping("/crfs")
    public Response<String> crfs(){
        return Response.success("请求成功！");
    }



    public static void main(String[] args) {
        String s = "aki";
        String orderCode = OrderUtil.getOrderCode((long) 1);

        System.out.println(GetIp());
    }

    public static String GetIp() {
        InetAddress ia = null;
        try {
            ia = InetAddress.getLocalHost();
            return ia.getHostAddress();
        } catch (Exception e) {
            return null;
        }
    }
}
