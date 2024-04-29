package com.demo.smart.server.session.redis;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.demo.smart.server.model.CodeResponse;
import com.demo.smart.server.session.ICodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.concurrent.TimeUnit;

/**
 * 分布式授权码管理
 * 
 * @author Joe
 */
@Component
@ConditionalOnProperty(name = "sso.session.manager", havingValue = "redis")
public class RedisCodeManager implements ICodeService {

	@Autowired
	private RedisTemplate redisTemplate;

	@Override
	public void create(String code, CodeResponse codeContent) {
		redisTemplate.opsForValue().set(code, JSON.toJSONString(codeContent), getExpiresIn(), TimeUnit.SECONDS);
	}

	@Override
	public CodeResponse getAndRemove(String code) {
		ValueOperations<String, String> operation = redisTemplate.opsForValue();
		String cc = operation.get(code);
		if (!StringUtils.isEmpty(cc)) {
			redisTemplate.delete(code);
		}
		return JSONObject.parseObject(cc, CodeResponse.class);
	}
}
