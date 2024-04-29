package com.demo.smart.server.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccessTokenResponse implements Serializable {
	private CodeResponse codeContent;
	private SsoUserResponse user;
	private String appId;
}