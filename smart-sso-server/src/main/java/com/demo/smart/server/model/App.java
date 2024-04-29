package com.demo.smart.server.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 应用
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class App implements Serializable {

	/** 名称 */
	private String name;
	/** 应用唯一标识 */
	private String appId;
	/** 应用密钥 */
	private String appSecret;
}
