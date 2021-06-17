/*
* eVA
* Version: 3.1.0
* Copyright (C) 2020 everis Spain S.A
* Date: 01 January 2020
* Author: everis bots@everis.com
* All rights reserved
*/
package com.everis.eva.service;

import java.io.UnsupportedEncodingException;
import java.util.Collections;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.everis.eva.controller.MsbotController;
import com.everis.eva.controller.dto.authorization.AuthorizationRequest;
import com.everis.eva.controller.dto.authorization.AuthorizationResponse;
import com.everis.eva.controller.dto.authorization.RefreshRequest;
import com.everis.eva.model.BrokerInfo;
import com.everis.eva.util.StringService;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

@Service
public class AuthorizationService {
	
	private static final Logger logger = LoggerFactory.getLogger(AuthorizationService.class);
	
	@Value("${keycloak.clientId}")
	private String clientId;
    
	@Value("${keycloak.username}")
	private String username;
	
	@Value("${keycloak.password}")
	private String password;
	
	@Value("${keycloak.grantType}")
	private String grantType;
	
	@Value("${keycloak.clientSecret}")
	private String clientSecret;
	
	@Value("${keycloak.hostUrl}")
	private String hostUrl;
	
	@Value("${keycloak.endpoint}")
	private String endpoint;
	
	@Autowired
	private RestTemplate restTemplate;
	
	public AuthorizationResponse getAuthorization(BrokerInfo brokerInfo, String msbotChatId) {
		
		final String authorizationUrl = hostUrl + endpoint;
		final AuthorizationRequest authorizationRequest = new AuthorizationRequest(clientId, username, password, grantType, clientSecret);
		final HttpHeaders headers = getHeaders(brokerInfo, msbotChatId);
		HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<MultiValueMap<String, String>>(authorizationRequest.getApplicationForm(), headers);

		logger.debug("Requesting authorization");

		final ResponseEntity<AuthorizationResponse> authorizationResponseEntity = restTemplate
				.postForEntity(authorizationUrl, httpEntity, AuthorizationResponse.class);
		logger.debug(String.format("Success on requesting authorization, HTTP status code: %s", Integer.toString(authorizationResponseEntity.getStatusCodeValue())));

		return authorizationResponseEntity.getBody();
	}
	
	public AuthorizationResponse refreshAuthorization(BrokerInfo brokerInfo, String msbotChatId, String refreshToken) {
		
		final String authorizationUrl = hostUrl + endpoint;
		final String grantType = "refresh_token";
		final RefreshRequest refreshRequest = new RefreshRequest(clientId, grantType, clientSecret, refreshToken);
		final HttpHeaders headers = getHeaders(brokerInfo, msbotChatId);
		HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<MultiValueMap<String, String>>(refreshRequest.getApplicationForm(), headers);

		logger.debug("Requesting refresh authorization");

		final ResponseEntity<AuthorizationResponse> authorizationResponseEntity = restTemplate
				.postForEntity(authorizationUrl, httpEntity, AuthorizationResponse.class);
		logger.debug( String.format("Success on requesting authorization, HTTP status code: %s", Integer.toString(authorizationResponseEntity.getStatusCodeValue())));

		return authorizationResponseEntity.getBody();
	}
	
	private HttpHeaders getHeaders(BrokerInfo info, String msbotChatId ) {
		HttpHeaders headers = new HttpHeaders();
		headers.add("APIKEY", info.getApiKey());
		headers.add("PROJECT", info.getProject());
		headers.add("CHANNEL", info.getChannel());
		headers.add("OPERATINGSYSTEM", info.getOs());
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		headers.add("LOCALE", info.getLocale());
		headers.add("USERREF", StringService.replaceHTTPRequestValue(msbotChatId));
		headers.add("TEST", "false");

		return headers;
	}
	
	
}
