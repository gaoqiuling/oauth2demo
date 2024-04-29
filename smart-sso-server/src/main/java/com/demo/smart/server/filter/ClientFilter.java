package com.demo.smart.server.filter;

import com.demo.smart.server.model.AuthParam;
import com.demo.smart.server.session.ISessionMappingStorageService;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Filter基类
 * 
 * @author Joe
 */
public abstract class ClientFilter extends AuthParam implements Filter {
	
	private ISessionMappingStorageService sessionMappingStorage;
    
	public abstract boolean isAccessAllowed(HttpServletRequest request, HttpServletResponse response)
			throws IOException;

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException {
	}

	@Override
	public void destroy() {
	}
	
	protected ISessionMappingStorageService getSessionMappingStorage() {
		if (sessionMappingStorage == null) {
            sessionMappingStorage = LogoutListener.getSessionMappingStorage();
        }
		return sessionMappingStorage;
	}
}