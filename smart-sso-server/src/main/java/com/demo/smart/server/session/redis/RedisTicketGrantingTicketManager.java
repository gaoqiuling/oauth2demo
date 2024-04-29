package com.demo.smart.server.session.redis;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.demo.smart.server.model.SsoUserResponse;
import com.demo.smart.server.session.ITicketGrantingTicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.concurrent.TimeUnit;

/**
 * 分布式登录凭证管理
 *
 * @author Joe
 */
@Component
@ConditionalOnProperty(name = "sso.session.manager", havingValue = "redis")
public class RedisTicketGrantingTicketManager implements ITicketGrantingTicketService {
    @Value("${sso.timeout}")
    private int timeout;
    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public void create(String tgt, SsoUserResponse user) {
        redisTemplate.opsForValue().set(tgt, JSON.toJSONString(user), getExpiresIn(), TimeUnit.SECONDS);
    }

    @Override
    public SsoUserResponse getAndRefresh(String tgt) {
        ValueOperations<String, String> operation = redisTemplate.opsForValue();
        String user = operation.get(tgt);
        if (StringUtils.isEmpty(user)) {
            return null;
        }
        redisTemplate.expire(tgt, timeout, TimeUnit.SECONDS);
        return JSONObject.parseObject(user, SsoUserResponse.class);
    }

    @Override
    public void set(String tgt, SsoUserResponse user) {
        create(tgt, user);
    }

    @Override
    public void remove(String tgt) {
        redisTemplate.delete(tgt);
    }

    @Override
    public int getExpiresIn() {
        return timeout;
    }
}