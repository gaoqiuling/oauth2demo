package com.demo.smart.server.session.redis;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.demo.smart.server.model.AccessTokenResponse;
import com.demo.smart.server.session.IAccessTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * 分布式调用凭证管理
 *
 * @author Joe
 */
@Component
@ConditionalOnProperty(name = "sso.session.manager", havingValue = "redis")
public class RedisAccessTokenManager implements IAccessTokenService {

    @Value("${sso.timeout}")
    private int timeout;
    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public void create(String accessToken, AccessTokenResponse accessTokenContent) {
        redisTemplate.opsForValue().set(accessToken, JSON.toJSONString(accessTokenContent), getExpiresIn(),
                TimeUnit.SECONDS);

        redisTemplate.opsForSet().add(getKey(accessTokenContent.getCodeContent().getTgt()), accessToken);
    }

    @Override
    public AccessTokenResponse get(String accessToken) {
        ValueOperations<String, String> operation = redisTemplate.opsForValue();
        String atcStr = operation.get(accessToken);
        if (StringUtils.isEmpty(atcStr)) {
            return null;
        }
        return JSONObject.parseObject(atcStr, AccessTokenResponse.class);
    }

    @Override
    public boolean refresh(String accessToken) {
        if (redisTemplate.opsForValue().get(accessToken) == null) {
            return false;
        }
        redisTemplate.expire(accessToken, timeout, TimeUnit.SECONDS);
        return true;
    }

    @Override
    public void remove(String tgt) {
        Set<String> accessTokenSet = redisTemplate.opsForSet().members(getKey(tgt));
        if (CollectionUtils.isEmpty(accessTokenSet)) {
            return;
        }
        redisTemplate.delete(getKey(tgt));

        accessTokenSet.forEach(accessToken -> {
            ValueOperations<String, String> operation = redisTemplate.opsForValue();
            String atcStr = operation.get(accessToken);
            if (StringUtils.isEmpty(atcStr)) {
                return;
            }
            AccessTokenResponse accessTokenContent = JSONObject.parseObject(atcStr, AccessTokenResponse.class);
            if (accessTokenContent == null || !accessTokenContent.getCodeContent().isSendLogoutRequest()) {
                return;
            }
            sendLogoutRequest(accessTokenContent.getCodeContent().getRedirectUri(), accessToken);
        });
    }

    private String getKey(String tgt) {
        return tgt + "_access_token";
    }

    /**
     * accessToken时效为登录session时效的1/2
     */
    @Override
    public int getExpiresIn() {
        return timeout / 2;
    }
}
