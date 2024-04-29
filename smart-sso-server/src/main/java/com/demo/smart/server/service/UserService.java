package com.demo.smart.server.service;

import com.demo.smart.server.model.Result;
import com.demo.smart.server.model.SsoUserResponse;

/**
 * 用户服务接口
 * 
 * @author Joe
 */
public interface UserService {
	
	/**
	 * 登录
	 * 
	 * @param username
	 *            登录名
	 * @param password
	 *            密码
	 * @return
	 */
	public Result<SsoUserResponse> login(String username, String password);
}
