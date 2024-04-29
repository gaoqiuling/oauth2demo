package com.demo.smart.server.controller;

import com.demo.smart.server.constant.Oauth2Constant;
import com.demo.smart.server.enums.GrantTypeEnum;
import com.demo.smart.server.model.*;
import com.demo.smart.server.service.AppService;
import com.demo.smart.server.service.UserService;
import com.demo.smart.server.session.IAccessTokenService;
import com.demo.smart.server.session.ICodeService;
import com.demo.smart.server.session.IRefreshTokenService;
import com.demo.smart.server.session.ITicketGrantingTicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Oauth2服务管理
 *
 * @author Joe
 */
@SuppressWarnings("rawtypes")
@RestController
@RequestMapping("/oauth2")
public class Oauth2Controller {

    @Autowired
    private AppService appService;
    @Autowired
    private UserService userService;

    @Autowired
    private ICodeService codeManager;
    @Autowired
    private IAccessTokenService accessTokenManager;
    @Autowired
    private IRefreshTokenService refreshTokenManager;
    @Autowired
    private ITicketGrantingTicketService ticketGrantingTicketManager;

    /**
     * 获取accessToken
     *
     * @param appId
     * @param appSecret
     * @param code
     * @return
     */
    @RequestMapping(value = "/access_token", method = RequestMethod.GET)
    public Result getAccessToken(
            @RequestParam(value = Oauth2Constant.GRANT_TYPE, required = true) String grantType,
            @RequestParam(value = Oauth2Constant.APP_ID, required = true) String appId,
            @RequestParam(value = Oauth2Constant.APP_SECRET, required = true) String appSecret,
            @RequestParam(value = Oauth2Constant.AUTH_CODE, required = false) String code,
            @RequestParam(value = Oauth2Constant.USERNAME, required = false) String username,
            @RequestParam(value = Oauth2Constant.PASSWORD, required = false) String password) {

        // 校验基本参数
        Result<Void> result = validateParam(grantType, code, username, password);
        if (!result.isSuccess()) {
            return result;
        }

        // 校验应用
        Result<Void> appResult = appService.validate(appId, appSecret);
        if (!appResult.isSuccess()) {
            return appResult;
        }

        // 校验授权
        Result<AccessTokenResponse> accessTokenResult = validateAuth(grantType, code, username, password, appId);
        if (!accessTokenResult.isSuccess()) {
            return accessTokenResult;
        }

        // 生成RpcAccessToken返回
        RpcAccessToken token = genereateRpcAccessToken(accessTokenResult.getData(), null);
        return Result.createSuccess(token);
    }

    private Result<Void> validateParam(String grantType, String code, String username, String password) {
        if (GrantTypeEnum.AUTHORIZATION_CODE.getValue().equals(grantType)) {
            if (StringUtils.isEmpty(code)) {
                return Result.createError("code不能为空");
            }
        } else if (GrantTypeEnum.PASSWORD.getValue().equals(grantType)) {
            if (StringUtils.isEmpty(username) || StringUtils.isEmpty(password)) {
                return Result.createError("username和password不能为空");
            }
        } else {
            return Result.createError("授权方式不支持");
        }
        return Result.success();
    }

    private Result<AccessTokenResponse> validateAuth(String grantType, String code, String username, String password,
                                                     String appId) {
        AccessTokenResponse authDto = null;
        if (GrantTypeEnum.AUTHORIZATION_CODE.getValue().equals(grantType)) {
            CodeResponse codeContent = codeManager.getAndRemove(code);
            if (codeContent == null) {
                return Result.createError("code有误或已过期");
            }

            SsoUserResponse user = ticketGrantingTicketManager.getAndRefresh(codeContent.getTgt());
            if (user == null) {
                return Result.createError("服务端session已过期");
            }
            authDto = new AccessTokenResponse(codeContent, user, appId);
        } else if (GrantTypeEnum.PASSWORD.getValue().equals(grantType)) {
            // app通过此方式由客户端代理转发http请求到服务端获取accessToken
            Result<SsoUserResponse> loginResult = userService.login(username, password);
            if (!loginResult.isSuccess()) {
                return Result.createError(loginResult.getMessage());
            }
            SsoUserResponse user = loginResult.getData();
            String tgt = ticketGrantingTicketManager.generate(loginResult.getData());
            CodeResponse codeContent = new CodeResponse(tgt, false, null);
            authDto = new AccessTokenResponse(codeContent, user, appId);
        }
        return Result.createSuccess(authDto);
    }

    /**
     * 刷新accessToken，并延长TGT超时时间
     * <p>
     * accessToken刷新结果有两种：
     * 1. 若accessToken已超时，那么进行refreshToken会生成一个新的accessToken，新的超时时间；
     * 2. 若accessToken未超时，那么进行refreshToken不会改变accessToken，但超时时间会刷新，相当于续期accessToken。
     *
     * @param appId
     * @param refreshToken
     * @return
     */
    @RequestMapping(value = "/refresh_token", method = RequestMethod.GET)
    public Result refreshToken(
            @RequestParam(value = Oauth2Constant.APP_ID, required = true) String appId,
            @RequestParam(value = Oauth2Constant.REFRESH_TOKEN, required = true) String refreshToken) {
        if (!appService.exists(appId)) {
            return Result.createError("非法应用");
        }

        RefreshTokenResponse refreshTokenContent = refreshTokenManager.validate(refreshToken);
        if (refreshTokenContent == null) {
            return Result.createError("refreshToken有误或已过期");
        }
        AccessTokenResponse accessTokenContent = refreshTokenContent.getAccessTokenContent();
        if (!appId.equals(accessTokenContent.getAppId())) {
            return Result.createError("非法应用");
        }
        SsoUserResponse user = ticketGrantingTicketManager.getAndRefresh(accessTokenContent.getCodeContent().getTgt());
        if (user == null) {
            return Result.createError("服务端session已过期");
        }

        return Result.createSuccess(genereateRpcAccessToken(accessTokenContent, refreshTokenContent.getAccessToken()));
    }

    private RpcAccessToken genereateRpcAccessToken(AccessTokenResponse accessTokenContent, String accessToken) {
        String newAccessToken = accessToken;
        if (newAccessToken == null || !accessTokenManager.refresh(newAccessToken)) {
            newAccessToken = accessTokenManager.generate(accessTokenContent);
        }

        String refreshToken = refreshTokenManager.generate(accessTokenContent, newAccessToken);

        return new RpcAccessToken(newAccessToken, accessTokenManager.getExpiresIn(), refreshToken,
                accessTokenContent.getUser());
    }
}