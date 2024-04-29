package com.demo.smart.server.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 用户
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User implements Serializable {
	/** ID */
	private Integer id;
	/** 姓名 */
	private String name;
	/** 登录名 */
	private String username;
	/** 密码 */
	private String password;
}
