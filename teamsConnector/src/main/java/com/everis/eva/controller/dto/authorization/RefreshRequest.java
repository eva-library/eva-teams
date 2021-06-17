package com.everis.eva.controller.dto.authorization;

import java.io.Serializable;

import org.springframework.beans.factory.annotation.Value;

import com.google.gson.annotations.SerializedName;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

public class RefreshRequest implements Serializable {
	private static final long serialVersionUID = 1L;
	
	
	public RefreshRequest(String clientId, String grantType,
			String clientSecret, String refreshToken) {
		super();
		this.clientId = clientId;
		this.grantType = grantType;
		this.clientSecret = clientSecret;
		this.refreshToken = refreshToken;
	}

	@SerializedName("client_id")
	private String clientId;
    
	@SerializedName("grant_type")
	private String grantType;
	
	@SerializedName("client_secret")
	private String clientSecret;
	
	@SerializedName("refresh_token")
	private String refreshToken;

	public MultiValueMap<String, String> getApplicationForm() {
		MultiValueMap<String, String> map= new LinkedMultiValueMap<String, String>();
		map.add("client_id", this.clientId);
		map.add("grant_type", this.grantType);
		map.add("client_secret", this.clientSecret);
		map.add("refresh_token", this.refreshToken);
		
		return map;
	}
}