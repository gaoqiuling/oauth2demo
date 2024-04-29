package com.demo.smart.server.model;

import lombok.Data;

/**
 * 参数注入Filter
 * 
 * @author Joe
 */
@Data
public class AuthParam {

	private String appId;
	private String appSecret;
	private String serverUrl;
}