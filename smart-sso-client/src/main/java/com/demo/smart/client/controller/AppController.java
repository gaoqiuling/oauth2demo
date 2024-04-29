package com.demo.smart.client.controller;

import com.demo.smart.client.constant.Oauth2Constant;
import com.demo.smart.client.model.AccessTokenResponse;
import com.demo.smart.client.model.Result;
import com.demo.smart.client.model.SsoUserResponse;
import com.demo.smart.client.util.Oauth2Utils;
import com.demo.smart.client.util.SessionUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Joe
 *
 */
@SuppressWarnings("rawtypes")
@RestController
@RequestMapping("/app")
public class AppController {
	
	@Value("${sso.server.url}")
    private String serverUrl;
    @Value("${sso.app.id}")
    private String appId;
    @Value("${sso.app.secret}")
    private String appSecret;

	/**
	 * 初始页
	 * @param request
	 * @param model
	 * @return
	 */
    @RequestMapping
	public Result index(HttpServletRequest request) {
		SsoUserResponse user = SessionUtils.getUser(request);
		return Result.createSuccess(user);
	}
	
	/**
	 * 登录提交
	 * 
	 * @param username
	 * @param password
	 * @param request
	 * @return
	 */
	@RequestMapping("/login")
	public Result login(
			@RequestParam(value = Oauth2Constant.USERNAME, required = true) String username,
			@RequestParam(value = Oauth2Constant.PASSWORD, required = true) String password,
			HttpServletRequest request) {
		Result<AccessTokenResponse> result = Oauth2Utils.getAccessToken(serverUrl, appId, appSecret, username, password);
		if (!result.isSuccess()) {
			return result;
		}
		SessionUtils.setAccessToken(request, result.getData());
		return Result.createSuccess().setMessage("登录成功");
	}
}
