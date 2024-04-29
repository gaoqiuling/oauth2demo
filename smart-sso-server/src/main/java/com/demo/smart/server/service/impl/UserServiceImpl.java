package com.demo.smart.server.service.impl;

import com.demo.smart.server.model.Result;
import com.demo.smart.server.model.SsoUserResponse;
import com.demo.smart.server.model.User;
import com.demo.smart.server.service.UserService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {
	
	private static List<User> userList;
	
	static {
		userList = new ArrayList<>();
		userList.add(new User(1, "管理员", "admin", "123456"));
	}
	
	@Override
	public Result<SsoUserResponse> login(String username, String password) {
		for (User user : userList) {
			if (user.getUsername().equals(username)) {
				if(user.getPassword().equals(password)) {
					return Result.createSuccess(new SsoUserResponse(user.getId(), user.getUsername()));
				}
				else {
					return Result.createError("密码有误");
				}
			}
		}
		return Result.createError("用户不存在");
	}
}
