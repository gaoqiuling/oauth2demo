package com.demo.smart.server.common;

/**
 * 含时效
 * 
 * @author Joe
 */
public interface Expiration {
	
	/**
	 * 时效（秒）
	 * @return
	 */
	int getExpiresIn();
}
