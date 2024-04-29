package com.demo.smart.client.session.redis;

import com.demo.smart.client.session.SessionMappingStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.session.SessionRepository;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpSession;

/**
 * 借鉴CAS
 *
 * @author Joe
 */
@Component
@ConditionalOnProperty(name = "sso.session.manager", havingValue = "redis")
public final class RedisSessionMappingStorage implements SessionMappingStorage {
    @Autowired
    private SessionRepository sessionRepository;
    @Autowired
    private RedisTemplate redisTemplate;

    private static final String SESSION_TOKEN_KEY = "session_token_key_";
    private static final String TOKEN_SESSION_KEY = "token_session_key_";

    @Override
    public synchronized void addSessionById(final String accessToken, final HttpSession session) {
        redisTemplate.opsForValue().set(SESSION_TOKEN_KEY + session.getId(), accessToken);
        redisTemplate.opsForValue().set(TOKEN_SESSION_KEY + accessToken, session.getId());
    }

    @Override
    public synchronized void removeBySessionById(final String sessionId) {
        ValueOperations<String, String> operation = redisTemplate.opsForValue();
        final String accessToken = operation.get(SESSION_TOKEN_KEY + sessionId);
        if (accessToken != null) {
            redisTemplate.delete(TOKEN_SESSION_KEY + accessToken);
            redisTemplate.delete(SESSION_TOKEN_KEY + sessionId);

            sessionRepository.deleteById(sessionId);
        }
    }

    @Override
    public synchronized HttpSession removeSessionByMappingId(final String accessToken) {
        ValueOperations<String, String> operation = redisTemplate.opsForValue();
        final String sessionId = operation.get(TOKEN_SESSION_KEY + accessToken);
        if (sessionId != null) {
            removeBySessionById(sessionId);
        }
        return null;
    }
}
