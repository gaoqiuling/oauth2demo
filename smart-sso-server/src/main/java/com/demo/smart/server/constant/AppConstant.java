package com.demo.smart.server.constant;

/**
 * 应用常量
 * 
 * @author Joe
 */
public class AppConstant {
    
    // 存cookie中TGT名称，和Cas保存一致
    public static final String TGC = "TGC";
    
    // 登录页
    public static final String LOGIN_PATH = "/login";

    public static final String[] JSON_WHITELIST_STR = { "org.springframework", "com.smart" };
}
