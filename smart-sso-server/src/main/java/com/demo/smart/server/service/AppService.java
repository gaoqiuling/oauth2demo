package com.demo.smart.server.service;


import com.demo.smart.server.model.Result;

/**
 * 应用服务接口
 * 
 * @author Joe
 */
public interface AppService {

	boolean exists(String appId);
	
	Result<Void> validate(String appId, String appSecret);
}
