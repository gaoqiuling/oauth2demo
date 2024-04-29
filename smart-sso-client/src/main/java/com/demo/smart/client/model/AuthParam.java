package com.demo.smart.client.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 参数注入Filter
 * 
 * @author Joe
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthParam {

	private String appId;
	private String appSecret;
	private String serverUrl;
}