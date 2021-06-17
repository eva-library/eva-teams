/*
* eVA
* Version: 3.1.0
* Copyright (C) 2020 everis Spain S.A
* Date: 01 January 2020
* Author: everis bots@everis.com
* All rights reserved
*/
package com.everis.eva.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import static com.everis.eva.Constants.CONTEXT_LABEL;
import static com.everis.eva.Constants.HEADER_API_KEY;
import static com.everis.eva.Constants.HEADER_PROJECT;
import static com.everis.eva.Constants.HEADER_CHANNEL;
import static com.everis.eva.Constants.HEADER_OS;
import static com.everis.eva.Constants.HEADER_LOCALE;
import static com.everis.eva.Constants.HEADER_USER_REF;
import static com.everis.eva.Constants.HEADER_AUTHORIZATION;
import com.everis.eva.controller.dto.authorization.AuthorizationResponse;
import com.everis.eva.controller.dto.broker.AnswerResponse;
import com.everis.eva.controller.dto.broker.ConversationRequestBody;
import com.everis.eva.controller.dto.broker.ConversationResponse;
import com.everis.eva.controller.dto.msbot.response.MsbotResponse;
import com.everis.eva.model.BrokerInfo;
import com.everis.eva.service.cache.MsbotCache;
import com.google.gson.Gson;

@Service
public class BrokerService {
	
	private static final Logger logger = LoggerFactory.getLogger(BrokerService.class);
	
	private static final String BROKER_ENDPOINT = "/conversations/";
	private static final String SESSION_LABEL = "sessionCode";
	private static final String AUTHORIZATION_LABEL = "authorization";
	private static final String REFRESH_TOKEN_LABEL = "refreshToken";
	
	@Value("#{${channel.id.map}}")
	private Map<String, String> channelIdChannelNameMap;
	
	@Value("${channel.id.default}")
	private String channelIdDefault;
	
	@Value("${eva.server.broker}")
	private String brokerBaseUrl;
	@Value("${broker.header.os}")
	private String brokerOs;
	@Value("${broker.apikey}")
	private String brokerApiKey;
	@Value("${broker.bot.name}")
	private String brokerBotName;
	@Value("${broker.bot.locale}")
	private String brokerBotLocale;

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private MsbotCache cache;

	@Autowired
	private Environment env;
	
	@Autowired
	private AuthorizationService authorizationService;

	public ConversationResponse sendMessage(String text, String code, String msChatId, String msChannelId) {		
		logger.info("MsBot ChatId: " + msChatId + " MsBot ChannelId: " + msChannelId);

		final Map<String, Object> userKeys = cache.getUserKeys(msChatId);
		String sessionId = "";
		String authorization = "";
		
		if (userKeys != null) { 
			if (userKeys.get(SESSION_LABEL) != null) {
				sessionId = userKeys.get(SESSION_LABEL).toString();
			}
			if(userKeys.get(AUTHORIZATION_LABEL) != null) {
				authorization = userKeys.get(AUTHORIZATION_LABEL).toString();
			}
		}
		
		final BrokerInfo brokerInfo = getProperties(msChannelId);
		HttpHeaders headers = getHeaders(brokerInfo, msChatId, authorization);
		
		try{
			final String hostUrl = brokerInfo.getBaseUrl();
			final String endpoint = (null == sessionId) ? BROKER_ENDPOINT : BROKER_ENDPOINT + sessionId;
			final String brokerUrl = hostUrl + endpoint;
			
			logger.info("brokerUrl: '" + brokerUrl + "'");
			
			try {
				ResponseEntity<ConversationResponse> responseEntity = sendRequest(text, code, msChatId, headers, brokerUrl);
				ConversationResponse response = processResponseSuccess(msChatId, userKeys, responseEntity);
				return response;
			} catch (HttpClientErrorException httpClientErrorException) {
				if (httpClientErrorException.getRawStatusCode() == 401) {
					return refreshAuthorizationRequest(text, code, msChatId, userKeys, brokerInfo, brokerUrl);
				} else {
					throw httpClientErrorException;
				}
			}
		}
		catch (final Exception e){
			logger.error(String.format("ERROR on requesting conversation: %s", e.getMessage()));
		}
		return null;
	}

	private ConversationResponse refreshAuthorizationRequest(final String text, final String code, final String chatId,
			Map<String, Object> userKeys, final BrokerInfo brokerInfo, final String brokerUrl) {
		
		if(userKeys != null && userKeys.get(REFRESH_TOKEN_LABEL) != null) {
			String refreshToken = userKeys.get(REFRESH_TOKEN_LABEL).toString();
			logger.error("The request has not been applied because it lacks valid authentication credentials for the target resource");
			try {
				AuthorizationResponse authorizationResponse = authorizationService.refreshAuthorization(brokerInfo, chatId, refreshToken);
				userKeys.put(AUTHORIZATION_LABEL, authorizationResponse.getAuthorization());
				userKeys.put(REFRESH_TOKEN_LABEL, authorizationResponse.getRefreshToken());				
				cache.setUserKeys(chatId, userKeys);
				
				logger.info("Retrying request with refreshed authorization parameter");
				HttpHeaders headers = getHeaders(brokerInfo, chatId, authorizationResponse.getAuthorization());
				ResponseEntity<ConversationResponse> responseEntity = sendRequest(text, code, chatId, headers, brokerUrl);
				ConversationResponse response = processResponseSuccess(chatId, userKeys, responseEntity);
				
				return response;
			} catch (HttpClientErrorException httpClientErrorException) {
				if (httpClientErrorException.getRawStatusCode() == 400) {
					logger.error("Invalid Grant, token is not active, getting new token");
					return sendAuthorizationRequest(text, code, chatId, userKeys, brokerInfo, brokerUrl);
				} else {
					throw httpClientErrorException;
				}
			}
			
		} else {
			return sendAuthorizationRequest(text, code, chatId, userKeys, brokerInfo, brokerUrl);
		}
	}
	
	private ConversationResponse sendAuthorizationRequest(final String text, final String code, final String chatId,
			Map<String, Object> userKeys, final BrokerInfo brokerInfo, final String brokerUrl) {

		logger.error("The request has not been applied because it lacks valid authentication credentials for the target resource");
		AuthorizationResponse authorizationResponse = authorizationService.getAuthorization(brokerInfo, chatId);
		if(userKeys == null){
			userKeys = new HashMap<>();
		}
		userKeys.put(AUTHORIZATION_LABEL, authorizationResponse.getAuthorization());
		userKeys.put(REFRESH_TOKEN_LABEL, authorizationResponse.getRefreshToken());		
		cache.setUserKeys(chatId, userKeys);
		
		logger.info("Retrying request with new authorization parameter");
		HttpHeaders headers = getHeaders(brokerInfo, chatId, authorizationResponse.getAuthorization());
		ResponseEntity<ConversationResponse> responseEntity = sendRequest(text, code, chatId, headers, brokerUrl);
		ConversationResponse response = processResponseSuccess(chatId, userKeys, responseEntity);
		
		return response;
	}

	private String getChannelName(String msChannelId) {
		final String channelMapName = channelIdChannelNameMap.get(msChannelId);
		String channelName = "";
		if (channelMapName == null) {
			logger.error("Broker Channel Id not found, chat Id: " + msChannelId + ". Setting Channel Id to default value " + channelIdDefault);			
			channelName = channelIdDefault;
		} else {
			channelName = channelMapName;
		}
		return channelName;
	}
	
	private ConversationResponse processResponseSuccess(String chatId, Map<String, Object> userKeys,
			final ResponseEntity<ConversationResponse> responseEntity) {
		ConversationResponse response;
		logger.info("Success on requesting conversation response");
		response = responseEntity.getBody();
		logger.info("This is our response body: " + response);
		Map<String, Object> context = response.getContext();
		if(userKeys == null) {
			userKeys = new HashMap<>();
			userKeys.put(SESSION_LABEL, response.getSessionCode());
		} else if (userKeys.get(SESSION_LABEL) == null){
			userKeys.put(SESSION_LABEL, response.getSessionCode());
		}
		userKeys.put(CONTEXT_LABEL, context);
		
		cache.removeUserKeysFromCache(chatId);
		cache.setUserKeys(chatId, userKeys);

		logger.debug(String.format("Response statusCode: %s", responseEntity.getStatusCodeValue()));
		return response;
	}
	
	private ResponseEntity<ConversationResponse> sendRequest(String text, String code, String chatId, final HttpHeaders headers,
			final String brokerUrl) {
	
		ConversationRequestBody conversationRequestBody;
		Map<String, Object> context = new HashMap<>();
		
		final Map<String, Object> userKeys = cache.getUserKeys(chatId);
		if (userKeys != null && userKeys.get(CONTEXT_LABEL) != null) {
			context = (Map<String, Object>) userKeys.get(CONTEXT_LABEL);
		}
		conversationRequestBody = new ConversationRequestBody(text, code, context);
		
		final String jsonRequest = new Gson().toJson(conversationRequestBody);
		final HttpEntity<String> httpEntity = new HttpEntity<>(jsonRequest, headers);
		
		logger.info("Sending request: " + httpEntity);
		logger.info("Requesting conversation response to broker, Broker Url: " + brokerUrl);

		final ResponseEntity<ConversationResponse> responseEntity = restTemplate
				.postForEntity(brokerUrl, httpEntity, ConversationResponse.class);
		return responseEntity;
	}

	public MsbotResponse setResponse(ConversationResponse conversationResponse) {
		final MsbotResponse msbotResponse = new MsbotResponse();
		final List<AnswerResponse> answerList = conversationResponse != null ? conversationResponse.getAnswers() : null;
		if (answerList != null && answerList.size() > 0) {
			msbotResponse.setText((String) answerList.get(0).getContent());
		}
		
		return msbotResponse;
	}
	
	private BrokerInfo getProperties(String msChannelId) {
		final BrokerInfo brokerInfo = new BrokerInfo();
		final String brokerChannelName = getChannelName(msChannelId);
		
		brokerInfo.setApiKey(brokerApiKey);
		brokerInfo.setBaseUrl(brokerBaseUrl);
		brokerInfo.setChannel(brokerChannelName);
		brokerInfo.setLocale(brokerBotLocale);
		brokerInfo.setOs(brokerOs);
		brokerInfo.setProject(brokerBotName);
		
		return brokerInfo;
	}

	private HttpHeaders getHeaders(BrokerInfo info, String chatId, String authorization) {
		final HttpHeaders headers = new HttpHeaders();
		
		headers.add(HEADER_API_KEY, info.getApiKey());
		headers.add(HEADER_PROJECT, info.getProject());
		headers.add(HEADER_CHANNEL, info.getChannel());
		headers.add(HEADER_OS, info.getOs());
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.add(HEADER_LOCALE, info.getLocale());
		headers.add(HEADER_USER_REF, chatId);
		headers.add(HEADER_AUTHORIZATION, authorization);

		return headers;
	}
}
