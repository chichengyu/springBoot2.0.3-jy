package com.jy.utils;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.UUID;


/**
 * 微信支付工具类，xml转map,map转xml，生成签名，验签，返回通知
 */
public class WXPayUtil {

    /** MD5 测试通过， HMAC-SHA256未测试
     * 生成微信支付sign
     * @param params  参数
     * @param key  商户密钥
     * @param signType  加密类型  MD5 / HMAC-SHA256
     * @return
     */
    public static String createSign(SortedMap<String, String> params, String key,String signType) throws Exception {
        String sign = "";
        if ("HMAC-SHA256".equals(signType)){
            // SHA256 加密时，需要加上 sign_type参数
            params.put("sign_type","HMAC-SHA256");
            sign = HMACSHA256(signStr(params,key),key);
        }else if ("MD5".equals(signType)){
            // MD5 加密
            sign = MD5(signStr(params,key));
        }
        return sign;
    }

    /** MD5 测试通过
     * 生成微信支付sign
     * @param params  参数
     * @param key  商户密钥
     * @return
     */
    public static String createMD5Sign(SortedMap<String, String> params, String key) throws Exception {
        return MD5(signStr(params,key));
    }

    /** MD5 测试通过， HMAC-SHA256未测试
     * 生成微信支付sign
     * @param params  参数
     * @param key  商户密钥
     * @return
     */
    public static String createHMACSHA256Sign(SortedMap<String, String> params, String key) throws Exception {
        // SHA256 加密时，需要加上 sign_type参数
        params.put("sign_type","HMAC-SHA256");
        return HMACSHA256(signStr(params,key),key);
    }

    /**
     * 处理参数
     * @param params
     * @param key
     * @return
     * @throws Exception
     */
    private static String signStr(SortedMap<String, String> params, String key) throws Exception {
        StringBuilder sb = new StringBuilder();
        //SortedMap<String, String> mapSortedMap = coverMap2SortedMap(params);// 字典排序
        Set<Map.Entry<String, String>> es =  params.entrySet();
        Iterator<Map.Entry<String,String>> it =  es.iterator();
        //生成 stringA="appid=wxd930ea5d5a258f4f&body=test&device_info=1000&mch_id=10000100&nonce_str=ibuaiVcKdpRxkhJA";
        while (it.hasNext()){
            Map.Entry<String,String> entry = (Map.Entry<String,String>)it.next();
            String k = (String)entry.getKey();
            String v = (String)entry.getValue();
            if(null != v && !"".equals(v) && !"sign".equals(k) && !"key".equals(k)){
                sb.append(k+"="+v+"&");
            }
        }
        //sb.append("key=").append(key);
        // 默认 MD5 加密 并转大写
        //String sign = MD5(sb.toString()).toUpperCase();
        return sb.append("key=").append(key).toString();
    }

    /**
     * 校验签名
     * @param params 参数
     * @param key   商户密钥
     * @param signType  加密类型  MD5 / HMAC-SHA256
     * @return
     */
    public static boolean checkSign(SortedMap<String, String> params, String key,String signType) throws Exception {
        String sign = createSign(params,key,signType);
        String weixinPaySign = params.get("sign").toUpperCase();
        return weixinPaySign.equals(sign);
    }

    /**
     * 校验签名（MD5）
     * @param params 参数
     * @param key   商户密钥
     * @return
     */
    public static boolean checkND5Sign(SortedMap<String, String> params, String key) throws Exception {
        String sign = createMD5Sign(params,key);
        String weixinPaySign = params.get("sign").toUpperCase();
        return weixinPaySign.equals(sign);
    }

    /**
     * 校验签名（HMAC-SHA256）
     * @param params 参数
     * @param key   商户密钥
     * @return
     */
    public static boolean checkHMACSHA256Sign(SortedMap<String, String> params, String key) throws Exception {
        String sign = createHMACSHA256Sign(params,key);
        String weixinPaySign = params.get("sign").toUpperCase();
        return weixinPaySign.equals(sign);
    }

    /**
     * 通知微信订单处理成功
     * @param returnCode  SUCCESS / FAIL
     * @return
     * @throws Exception
     */
    public static String returnXML(String returnCode) throws Exception {
        Map<String,String> map = new HashMap<>();
        map.put("return_code",returnCode);
        map.put("return_msg","OK");
        return mapToXml(map);
    }

    /** map 字典排序
     * Map 转换有序 SortedMap
     * @param map
     * @return
     */
    public static SortedMap<String,String> coverMap2SortedMap(Map<String,String> map){
        SortedMap<String, String> sortedMap = new TreeMap<>();
        Iterator<String> it =  map.keySet().iterator();
        while (it.hasNext()){
            String key  = (String)it.next();
            String value = map.get(key);
            String temp = "";
            if( null != value){
                temp = value.trim();
            }
            sortedMap.put(key,temp);
        }
        return sortedMap;
    }

    /**
     * 生成随机32位字符 nonce_str
     * @return
     */
    public static String generateUUID(){
        String uuid = UUID.randomUUID().toString().replaceAll("-","").substring(0,32);
        return uuid;
    }

    /**
     * md5加密
     * @param data
     * @return
     */
    public static String MD5(String data) throws Exception {
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        byte [] array = md5.digest(data.getBytes("UTF-8"));
        StringBuilder sb = new StringBuilder();
        for (byte item : array) {
            sb.append(Integer.toHexString((item & 0xFF) | 0x100).substring(1, 3));
        }
        return sb.toString().toUpperCase();
    }

    /**
     * HMACSHA256 加密
     * @param data
     * @return
     * @throws Exception
     */
    public static String HMACSHA256(String data, String key) throws Exception {
        Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
        SecretKeySpec secret_key = new SecretKeySpec(key.getBytes("UTF-8"), "HmacSHA256");
        sha256_HMAC.init(secret_key);
        byte[] array = sha256_HMAC.doFinal(data.getBytes("UTF-8"));
        StringBuilder sb = new StringBuilder();
        for (byte item : array) {
            sb.append(Integer.toHexString((item & 0xFF) | 0x100).substring(1, 3));
        }
        return sb.toString().toUpperCase();
    }

    /**
     * XML格式字符串转换为Map
     * @param strXML XML字符串
     * @return XML数据转换后的Map
     * @throws Exception
     */
    public static Map<String, String> xmlToMap(String strXML) throws Exception {
        try {
            Map<String, String> data = new HashMap<String, String>();
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            InputStream stream = new ByteArrayInputStream(strXML.getBytes("UTF-8"));
            org.w3c.dom.Document doc = documentBuilder.parse(stream);
            doc.getDocumentElement().normalize();
            NodeList nodeList = doc.getDocumentElement().getChildNodes();
            for (int idx = 0; idx < nodeList.getLength(); ++idx) {
                Node node = nodeList.item(idx);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    org.w3c.dom.Element element = (org.w3c.dom.Element) node;
                    data.put(element.getNodeName(), element.getTextContent());
                }
            }
            try {
                stream.close();
            } catch (Exception ex) {
                // do nothing
            }
            return data;
        } catch (Exception ex) {
            throw ex;
        }
    }

    /**
     * 将Map转换为XML格式的字符串
     * @param data Map类型数据
     * @return XML格式的字符串
     * @throws Exception
     */
    public static String mapToXml(Map<String, String> data) throws Exception {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder= documentBuilderFactory.newDocumentBuilder();
        org.w3c.dom.Document document = documentBuilder.newDocument();
        org.w3c.dom.Element root = document.createElement("xml");
        document.appendChild(root);
        for (String key: data.keySet()) {
            String value = data.get(key);
            if (value == null) {
                value = "";
            }
            value = value.trim();
            org.w3c.dom.Element filed = document.createElement(key);
            filed.appendChild(document.createTextNode(value));
            root.appendChild(filed);
        }
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer = tf.newTransformer();
        DOMSource source = new DOMSource(document);
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        StringWriter writer = new StringWriter();
        StreamResult result = new StreamResult(writer);
        transformer.transform(source, result);
        String output = writer.getBuffer().toString(); //.replaceAll("\n|\r", "");
        try {
            writer.close();
        }
        catch (Exception ex) {
        }
        return output;
    }

    /**
     * 获取用户 ip
     * @param request
     * @return
     */
    public static String getIpAddr(HttpServletRequest request) {
        String ipAddress = null;
        try {
            ipAddress = request.getHeader("x-forwarded-for");
            if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
                ipAddress = request.getHeader("Proxy-Client-IP");
            }
            if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
                ipAddress = request.getHeader("WL-Proxy-Client-IP");
            }
            if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
                ipAddress = request.getRemoteAddr();
                if (ipAddress.equals("127.0.0.1")) {
                    // 根据网卡取本机配置的IP
                    InetAddress inet = null;
                    try {
                        inet = InetAddress.getLocalHost();
                    } catch (UnknownHostException e) {
                        e.printStackTrace();
                    }
                    ipAddress = inet.getHostAddress();
                }
            }
            // 对于通过多个代理的情况，第一个IP为客户端真实IP,多个IP按照','分割
            if (ipAddress != null && ipAddress.length() > 15) { // "***.***.***.***".length()
                // = 15
                if (ipAddress.indexOf(",") > 0) {
                    ipAddress = ipAddress.substring(0, ipAddress.indexOf(","));
                }
            }
        } catch (Exception e) {
            ipAddress="";
        }
        return ipAddress;
    }


    // 测试
    public static void main(String[] args) throws Exception {
        //生成签名
        SortedMap<String,String> params = new TreeMap<>();
        params.put("appid","xxxxxxx");
        params.put("mch_id","xxxxxxx");
        params.put("nonce_str","xxxxxxx");
        params.put("body","xxxxxxx");
        params.put("out_trade_no","xxxxxxx");
        params.put("total_fee","xxxxxxx");
        params.put("spbill_create_ip","xxxxxxx");
        params.put("notify_url","xxxxxxx");
        params.put("trade_type","NATIVE");

        //sign签名    MD5 / HMAC-SHA256
        String sign = WXPayUtil.createSign(params,"xxxxxxxxxx","MD5");
        params.put("sign",sign);
        //map转xml，拿到 xml 去微信效验工具验证签名
        String payXml = WXPayUtil.mapToXml(params);
        //统一下单（post方式）
        //String orderStr = HttpUtils.doPost(WeChatConfig.getUnifiedOrderUrl(),payXml);
        //if(null == orderStr) {
        //    return null;
        //}
        //Map<String, String> unifiedOrderMap =  WXPayUtil.xmlToMap(orderStr);
        //System.out.println(unifiedOrderMap.toString());
        //if(unifiedOrderMap != null) {
        //    return unifiedOrderMap.get("code_url");
        //}
        //return null;
    }
}
