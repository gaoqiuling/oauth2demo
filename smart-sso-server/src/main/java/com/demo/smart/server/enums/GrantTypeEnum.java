package com.demo.smart.server.enums;

/**
 * Oauth2授权方式
 * 
 * @author Joe
 */
public enum GrantTypeEnum {
	
	/**
	 * 授权码模式
	 */
	AUTHORIZATION_CODE("authorization_code"), 

	/**
	 * 密码模式
	 */
	PASSWORD("password");

	private String value;

	private GrantTypeEnum(String value) {
		this.value = value;
	}

	public String getValue() {
		return this.value;
	}
}