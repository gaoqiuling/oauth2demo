package com.demo.smart.server.session;

import com.demo.smart.server.common.Expiration;
import com.demo.smart.server.model.CodeResponse;

import java.util.UUID;

/**
 * 授权码code管理
 * 
 * @author Joe
 */
public interface ICodeService extends Expiration {
	
	/**
	 * 生成授权码
	 * 
	 * @param tgt
	 * @param clientType
	 * @param redirectUri
	 * @return
	 */
	default String generate(String tgt, boolean sendLogoutRequest, String redirectUri) {
		String code = "code-" + UUID.randomUUID().toString().replaceAll("-", "");
		create(code, new CodeResponse(tgt, sendLogoutRequest, redirectUri));
		return code;
	}
    
    /**
     * 生成授权码
     * 
	 * @param code
	 * @param codeContent
	 */
	public void create(String code, CodeResponse codeContent) ;

    /**
     * 查找并删除
     * 
     * @param code
     * @return
     */
	CodeResponse getAndRemove(String code);
	
	/* 
	 * code失效时间默认为10分钟
	 */
	@Override
	default int getExpiresIn() {
		return 600;
	}
}
