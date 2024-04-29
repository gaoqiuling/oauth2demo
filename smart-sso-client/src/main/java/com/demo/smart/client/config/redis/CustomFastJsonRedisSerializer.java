package com.demo.smart.client.config.redis;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

import java.nio.charset.StandardCharsets;

public class CustomFastJsonRedisSerializer<T> implements RedisSerializer<T> {

    private final Class<T> type;
    private final SerializerFeature[] features;
    private final String dateFormat;

    public CustomFastJsonRedisSerializer(Class<T> type) {
        this(type, new SerializerFeature[0], null);
    }

    public CustomFastJsonRedisSerializer(Class<T> type, SerializerFeature[] features, String dateFormat) {
        this.type = type;
        this.features = features;
        this.dateFormat = dateFormat;
    }

    @Override
    public byte[] serialize(T object) throws SerializationException {
        if (object == null) {
            return new byte[0];
        }

        return JSON.toJSONString(object, features).getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public T deserialize(byte[] bytes) throws SerializationException {
        if (bytes == null || bytes.length == 0) {
            return null;
        }

        return JSON.parseObject(new String(bytes, StandardCharsets.UTF_8), type);
    }
}