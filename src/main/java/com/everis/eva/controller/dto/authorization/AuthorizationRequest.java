package com.everis.eva.controller.dto.authorization;

import java.io.Serializable;

import org.springframework.beans.factory.annotation.Value;

import com.google.gson.annotations.SerializedName;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

public class AuthorizationRequest implements Serializable {
	private static final long serialVersionUID = 1L;
	
	
	public AuthorizationRequest(String clientId, String username, String password, String grantType,
			String clientSecret) {
		super();
		this.clientId = clientId;
		this.username = username;
		this.password = password;
		this.grantType = grantType;
		this.clientSecret = clientSecret;
	}

	@SerializedName("client_id")
	private String clientId;
    
	@SerializedName("username")
	private String username;
	
	@SerializedName("password")
	private String password;
	
	@SerializedName("grant_type")
	private String grantType;
	
	@SerializedName("client_secret")
	private String clientSecret;

	public MultiValueMap<String, String> getApplicationForm() {
		MultiValueMap<String, String> map= new LinkedMultiValueMap<String, String>();
		map.add("client_id", this.clientId);
		map.add("username", this.username);
		map.add("password", this.password);
		map.add("grant_type", this.grantType);
		map.add("client_secret", this.clientSecret);
		
		return map;
	}
}
