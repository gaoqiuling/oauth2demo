package com.demo.smart.server.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 已登录用户信息
 * 
 * @author Joe
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SsoUserResponse implements Serializable {
	// 登录成功userId
    private Integer id;
    // 登录名
    private String username;
}
