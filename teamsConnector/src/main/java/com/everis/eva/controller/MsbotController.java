/*
 * eVA
 * Version: 3.1.0
 * Copyright (C) 2020 everis Spain S.A
 * Date: 01 January 2020
 * Author: everis bots@everis.com
 * All rights reserved
 */
package com.everis.eva.controller;

import com.codepoetics.protonpack.collectors.CompletableFutures;
import com.microsoft.bot.builder.ActivityHandler;
import com.microsoft.bot.builder.TurnContext;
import com.microsoft.bot.schema.ChannelAccount;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import com.everis.eva.service.MsbotService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import javax.annotation.PostConstruct;

@Component
public class MsbotController extends ActivityHandler {

	private static final Logger logger = LoggerFactory.getLogger(MsbotController.class);

	@Value("${spring.cache.type}")
	private String redisType;

	@Autowired
	private MsbotService msbotService;

	private final String startCode = "/start";
	private final String welcomeCode = "%EVA_WELCOME_MSG";

	@Override
	protected CompletableFuture<Void> onMessageActivity(TurnContext turnContext) {
		logger.info("onMessageActivity INIT");
		return turnContext.sendActivities(
				msbotService.getBotAnswers(turnContext, startCode.equals(turnContext.getActivity().getText()) ? welcomeCode : "")            
				).thenApply(sendResult -> null);
	}

	@Override
	protected CompletableFuture<Void> onMembersAdded(
			List<ChannelAccount> membersAdded,
			TurnContext turnContext
			) {
		turnContext.getActivity().setText(welcomeCode);
		return membersAdded.stream()
				.filter(
						member -> !StringUtils
						.equals(member.getId(), turnContext.getActivity().getRecipient().getId())
						).map(channel -> turnContext.sendActivities(
								msbotService.getBotAnswers(turnContext, welcomeCode)))
				.collect(CompletableFutures.toFutureList()).thenApply(resourceResponses -> null);
	}

	@PostConstruct
	public void start() {
		logger.info("Microsoft bot builder activity handler MsbotController started OK");
	}

}
