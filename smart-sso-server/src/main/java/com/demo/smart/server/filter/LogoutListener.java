package com.demo.smart.server.filter;

import com.demo.smart.server.session.ISessionMappingStorageService;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

/**
 * 单点登出Listener
 * <p>
 * 注：用于本地session过期，删除accessToken和session的映射关系
 *
 * @author Joe
 */
public final class LogoutListener implements HttpSessionListener {

    private static ISessionMappingStorageService sessionMappingStorage;

    @Override
    public void sessionCreated(final HttpSessionEvent event) {
    }

    @Override
    public void sessionDestroyed(final HttpSessionEvent event) {
        final HttpSession session = event.getSession();
        sessionMappingStorage.removeBySessionById(session.getId());
    }

    public void setSessionMappingStorage(ISessionMappingStorageService sms) {
        sessionMappingStorage = sms;
    }

    public static ISessionMappingStorageService getSessionMappingStorage() {
        return sessionMappingStorage;
    }
}
