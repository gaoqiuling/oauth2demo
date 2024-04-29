package com.demo.smart.server.session;

import com.demo.smart.server.common.Expiration;
import com.demo.smart.server.model.AccessTokenResponse;
import com.demo.smart.server.model.RefreshTokenResponse;

import java.util.UUID;

/**
 * 刷新凭证refreshToken管理抽象
 * 
 * @author Joe
 */
public interface IRefreshTokenService extends Expiration {

	/**
	 * 生成refreshToken
	 * 
	 * @param accessTokenContent
	 * @param accessToken
	 * @return
	 */
	default String generate(AccessTokenResponse accessTokenContent, String accessToken) {
		String resfreshToken = "RT-" + UUID.randomUUID().toString().replaceAll("-", "");
		create(resfreshToken, new RefreshTokenResponse(accessTokenContent, accessToken));
		return resfreshToken;
	}

	/**
	 * 生成refreshToken
	 * 
	 * @param refreshToken
	 * @param refreshTokenContent
	 */
	void create(String refreshToken, RefreshTokenResponse refreshTokenContent);

	/**
	 * 验证refreshToken有效性，无论有效性与否，都remove掉
	 * 
	 * @param refreshToken
	 * @return
	 */
	RefreshTokenResponse validate(String refreshToken);
}
