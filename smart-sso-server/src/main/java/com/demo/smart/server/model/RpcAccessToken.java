package com.demo.smart.server.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 服务端回传Token对象
 * 
 * @author Joe
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RpcAccessToken implements Serializable {
	/**
	 * 调用凭证
	 */
	private String accessToken;
	/**
	 * AccessToken超时时间，单位（秒）
	 */
	private int expiresIn;
	/**
	 * 当前AccessToken超时，用于刷新AccessToken并延长服务端session时效必要参数
	 */
	private String refreshToken;
	/**
	 * 用户信息
	 */
	private SsoUserResponse user;
}