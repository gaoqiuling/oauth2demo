package com.demo.smart.server.session;

import com.demo.smart.server.constant.AppConstant;
import com.demo.smart.server.model.SsoUserResponse;
import com.demo.smart.server.util.CookieUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 服务端凭证管理
 *
 * @author Joe
 */
@Component
public class SessionManager {

    @Autowired
    private IAccessTokenService accessTokenManager;
    @Autowired
    private ITicketGrantingTicketService ticketGrantingTicketManager;

    public String setUser(SsoUserResponse user, HttpServletRequest request, HttpServletResponse response) {
        String tgt = getCookieTgt(request);
        if (StringUtils.isEmpty(tgt)) {// cookie中没有
            tgt = ticketGrantingTicketManager.generate(user);

            // TGT存cookie，和Cas登录保存cookie中名称一致为：TGC
            CookieUtils.addCookie(AppConstant.TGC, tgt, "/", request, response);
        } else if (ticketGrantingTicketManager.getAndRefresh(tgt) == null) {
            ticketGrantingTicketManager.create(tgt, user);
        } else {
            ticketGrantingTicketManager.set(tgt, user);
        }
        return tgt;
    }

    public SsoUserResponse getUser(HttpServletRequest request) {
        String tgt = getCookieTgt(request);
        if (StringUtils.isEmpty(tgt)) {
            return null;
        }
        return ticketGrantingTicketManager.getAndRefresh(tgt);
    }

    public void invalidate(HttpServletRequest request, HttpServletResponse response) {
        String tgt = getCookieTgt(request);
        if (StringUtils.isEmpty(tgt)) {
            return;
        }
        // 删除登录凭证
        ticketGrantingTicketManager.remove(tgt);
        // 删除凭证Cookie
        CookieUtils.removeCookie(AppConstant.TGC, "/", response);
        // 删除所有tgt对应的调用凭证，并通知客户端登出注销本地session
        accessTokenManager.remove(tgt);
    }

    public String getTgt(HttpServletRequest request) {
        String tgt = getCookieTgt(request);
        if (StringUtils.isEmpty(tgt) || ticketGrantingTicketManager.getAndRefresh(tgt) == null) {
            return null;
        } else {
            return tgt;
        }
    }

    private String getCookieTgt(HttpServletRequest request) {
        return CookieUtils.getCookie(request, AppConstant.TGC);
    }
}