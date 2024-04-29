package com.demo.smart.server.session.redis;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.demo.smart.server.model.RefreshTokenResponse;
import com.demo.smart.server.session.IRefreshTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.concurrent.TimeUnit;

/**
 * 分布式刷新凭证管理
 * 
 * @author Joe
 */
@Component
@ConditionalOnProperty(name = "sso.session.manager", havingValue = "redis")
public class RedisRefreshTokenManager implements IRefreshTokenService {
	
	@Value("${sso.timeout}")
    private int timeout;
	@Autowired
	private RedisTemplate redisTemplate;

	@Override
	public void create(String refreshToken, RefreshTokenResponse refreshTokenContent) {
		redisTemplate.opsForValue().set(refreshToken, JSON.toJSONString(refreshTokenContent), getExpiresIn(),
				TimeUnit.SECONDS);
	}

	@Override
	public RefreshTokenResponse validate(String refreshToken) {
		ValueOperations<String, String> operation = redisTemplate.opsForValue();
		String rtc = operation.get(refreshToken);
		if (!StringUtils.isEmpty(rtc)) {
			redisTemplate.delete(refreshToken);
		}
		return JSONObject.parseObject(rtc, RefreshTokenResponse.class);
	}
	
	/*
	 * refreshToken时效和登录session时效一致
	 */
	@Override
	public int getExpiresIn() {
		return timeout;
	}
}
