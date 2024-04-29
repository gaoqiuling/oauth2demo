package com.demo.smart.client.filter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * APP单点登出Filter
 * 
 * @author Joe
 */
public class AppLogoutFilter extends LogoutFilter {

    @Override
    public boolean isAccessAllowed(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (getLogoutParam(request) != null) {
        	request.getSession().invalidate();
            return false;
        }
        return true;
    }
}