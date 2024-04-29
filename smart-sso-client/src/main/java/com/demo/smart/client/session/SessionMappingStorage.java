package com.demo.smart.client.session;

import javax.servlet.http.HttpSession;

/**
 * 借鉴CAS
 * 
 * @author Joe
 */
public interface SessionMappingStorage {

    HttpSession removeSessionByMappingId(String accessToken);

    void removeBySessionById(String sessionId);

    void addSessionById(String accessToken, HttpSession session);
}
