package com.demo.smart.server.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RefreshTokenResponse implements Serializable {
	private AccessTokenResponse accessTokenContent;

	private String accessToken;
}