# oauth2demo
oauth2示例代码

### jwt-oauth2-server

Authorization中使用type=Basic Auth,Username\:clientid,Password\:secret

获取accessToken: post <http://localhost:9000/oauth/token?grant_type=password&username=user&password=pass>

获取用户信息：get <http://localhost:9000/users/me?access_token=>xxx

> oauth2获取授权码,统一的url地址：/oauth/token。传入参数都要有：clientId,clientSecret,grantType

### jwt-oauth2-client

资源服务器里需要如下的配置类：

重点看@EnableResourceServer，tokenStore，tokenServices

```java
@Configuration
@EnableResourceServer
@EnableConfigurationProperties(SecurityProperties.class)
public class ResourceServerConfiguration extends ResourceServerConfigurerAdapter {
@Bean
public DefaultTokenServices tokenServices(final TokenStore tokenStore) {
    DefaultTokenServices tokenServices = new DefaultTokenServices();
    tokenServices.setTokenStore(tokenStore);
    return tokenServices;
}

@Bean
public TokenStore tokenStore() {
    if (tokenStore == null) {
        tokenStore = new JwtTokenStore(jwtAccessTokenConverter());
    }
    return tokenStore;
}
}
```

### smart-sso-server

server端-登录

1.  客户端访问，跳到服务端登录页，服务端。在服务端输入账号密码，点击登录，调登录接口，生成tgt,以tgt为key，存储用户信息，同时生成code,调回客户端页面
2.  客户端拿到code,调服务端获取accessToken接口，服务端生成accessToken存储在session里面。在redis的set里面记录（为注销时用到），记录以accessToken为key的用户信息

server端-登出

1.  删除redis缓存tgt，删除redis缓存accessToken,删除cookie
2.  获取redis中所有有该tgt下的所有远程服务地址，一一调用注销

### smart-sso-client

客户端-访问：

1.  所有请求都经过LoginFilter,如果请求里面有accessToken就放行，如果请求有code,自动去服务端拿accessToken，其余都跳到服务端登录页

客户端-登出：

1.  所有请求都经过LogoutFilter,如果请求头里面有logoutrequest,则清除token缓存和session缓存

