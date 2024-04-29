package com.demo.smart.server.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 授权存储信息
 * 
 * @author Joe
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponse implements Serializable {
	private String tgt;
	private boolean sendLogoutRequest;
	private String redirectUri;
}