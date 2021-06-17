/*
 * eVA
 * Version: 3.1.0
 * Copyright (C) 2020 everis Spain S.A
 * Date: 01 January 2020
 * Author: everis bots@everis.com
 * All rights reserved
 */
package com.everis.eva.service;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.everis.eva.Constants.CHANNEL_ID_MSTEAMS;
import static com.everis.eva.controller.dto.broker.answer.TypeConstants.TYPE_DOCUMENT;
import static com.everis.eva.controller.dto.broker.answer.TypeConstants.TYPE_AUDIO;
import static com.everis.eva.controller.dto.broker.answer.TypeConstants.TYPE_VIDEO;
import static com.everis.eva.controller.dto.broker.answer.TypeConstants.TYPE_IMAGE;
import static com.everis.eva.controller.dto.broker.answer.TypeConstants.TYPE_LIST;
import static com.everis.eva.controller.dto.broker.answer.TypeConstants.TYPE_TEXT;
import static com.everis.eva.controller.dto.broker.answer.TypeConstants.TYPE_TEXT_OPTIONS;
import static com.everis.eva.controller.dto.broker.answer.TypeConstants.TYPE_CAROUSEL;
import static com.everis.eva.controller.dto.broker.answer.TypeConstants.TYPE_CUSTOM;
import static com.everis.eva.controller.dto.broker.answer.TypeConstants.BUTTON_TYPE_URL;
import static com.everis.eva.controller.dto.broker.answer.TypeConstants.BUTTON_TYPE_FLOW;

import com.everis.eva.controller.dto.broker.AnswerResponse;
import com.everis.eva.controller.dto.broker.ConversationResponse;
import com.everis.eva.controller.dto.broker.answer.Button;
import com.everis.eva.controller.dto.broker.answer.Content;
import com.everis.eva.controller.dto.msbot.response.AdaptiveCard;
import com.everis.eva.controller.dto.msbot.response.TeamsMessagingExtensionsActionPreviewBot;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.bot.builder.MessageFactory;
import com.microsoft.bot.builder.TurnContext;
import com.microsoft.bot.schema.ActionTypes;
import com.microsoft.bot.schema.Activity;
import com.microsoft.bot.schema.Attachment;
import com.microsoft.bot.schema.CardAction;
import com.microsoft.bot.schema.CardImage;
import com.microsoft.bot.schema.HeroCard;
import com.microsoft.bot.schema.SuggestedActions;

@Service
public class MsbotService {

	private static final int CHAT_ID_MAX_LENGTH = 50;

	private static final Logger logger = LoggerFactory.getLogger(MsbotService.class);

	private final String EMPTY_ANSWER_TEXT= "Empty answer";

	@Autowired
	private BrokerService brokerService;

	public List<Activity> getBotAnswers(TurnContext turnContext, String code) {
		String inputMessageText = turnContext.getActivity().getText();
		if(inputMessageText == null) {
			try {
				ObjectMapper mapper = new ObjectMapper();
				String json = mapper.writeValueAsString(turnContext.getActivity().getValue());
				logger.info("TURNCONTEXT ENTERO");
				logger.info(json);
				JSONObject jsonObj = new JSONObject(json);
				inputMessageText = jsonObj.getString("action");
				logger.info(inputMessageText);
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			}
		}
		String msbotChannelId = turnContext.getActivity().getChannelId() != null ? turnContext.getActivity().getChannelId() : "emulator";
		String chatId = getChatId(turnContext);
		logger.info("inputMessageText: {}, msbotChannelId: {}", inputMessageText, msbotChannelId);

		final ConversationResponse conversationResponse = brokerService.sendMessage(inputMessageText, code, chatId, msbotChannelId);

		return processConversationResponse(conversationResponse, msbotChannelId);
	}

	private String getChatId(TurnContext turnContext) {
		String chatId = "";

		if(turnContext.getActivity().getFrom() != null && turnContext.getActivity().getFrom().getId() != null) {
			chatId = turnContext.getActivity().getFrom().getId();	
			if (chatId.length() > CHAT_ID_MAX_LENGTH) {
				chatId = chatId.substring(0, CHAT_ID_MAX_LENGTH);
			}
		} else {
			logger.error("Turn Context Activity From Id empty, Chat Id not been set");
		}
		return chatId;
	}

	private List<Activity> processConversationResponse(final ConversationResponse conversationResponse, String msbotChannelId) {
		logger.debug(conversationResponse != null ? conversationResponse.toString() : "ConversationResponse null");
		List<Activity> activities = new ArrayList<Activity>();
		final List<AnswerResponse> answerList = conversationResponse != null ? conversationResponse.getAnswers() : null;
		if (answerList != null) {
			logger.info("AnswerList size:" + answerList.size());
			for (final AnswerResponse answer : answerList) {
				String answerContent;
				String answerUrl;
				String answerContentType = "";
				String answerType = answer.getType();
				logger.info("Answer type: %s", answerType);
				
				switch (answerType) {
				case TYPE_TEXT:
					answerContent = (String) answer.getContent();
					if(StringUtils.isNotBlank(answerContent)){
						if(hasButtons(answer)) {
							activities.add(createHeroCard(answer));
						} else {
							activities.add(MessageFactory.text(answerContent));
						}
					}
					break;
				case TYPE_TEXT_OPTIONS:
					if(hasButtons(answer)) {
						switch (msbotChannelId) {
						case CHANNEL_ID_MSTEAMS:
							activities.add(createHeroCard(answer));
							break;
						default:
							activities.add(createSuggestedActions(answer));
						}
					} else {
						answerContent = (String) answer.getContent();
						if(StringUtils.isNotBlank(answerContent)){
							activities.add(MessageFactory.text(answerContent));
						}
					}
					break;
				case TYPE_IMAGE:
					answerUrl = (String) answer.getContent();
					if(StringUtils.isNotBlank(answerUrl)){
						answerContentType = URLConnection.guessContentTypeFromName(answerUrl);
						activities.add(MessageFactory.contentUrl(answerUrl, answerContentType));	
					}
					break;
				case TYPE_CAROUSEL:
					activities.add(createCarousel(answer));
					break;


				case TYPE_DOCUMENT:
				case TYPE_LIST:
				case TYPE_VIDEO:
				case TYPE_AUDIO:	
					logger.warn("Answer type " + answerType + " not supported");
					break;
				case TYPE_CUSTOM:
					//para las adatative cards
					activities.add(createAdaptativeCard(answer));
					break;

				default:
					logger.warn("Answer type " + answerType + " not supported");
				}
			}
		} else {
			activities.add(MessageFactory.text(EMPTY_ANSWER_TEXT));
		}

		return activities;
	}

	private boolean hasButtons(AnswerResponse answer) {
		List<Button> buttons = answer.getButtons();
		if (buttons != null && buttons.size() > 0) {
			return true;
		}
		return false;
	}

	private Activity createSuggestedActions(AnswerResponse answer) {
		String answerContent = (String) answer.getContent();
		try {
			List<Button> buttons = answer.getButtons();
			List<CardAction> cardActionList = getButtonCardActionList(buttons);

			Activity reply = MessageFactory.text(answerContent);

			reply.setSuggestedActions(new SuggestedActions() {
				{
					setActions(cardActionList);
				}
			});

			return reply;
		} catch (Exception e) {
			logger.error("Error parsing response", e);
			return MessageFactory.text(answerContent);
		}
	}

	private Activity createHeroCard(AnswerResponse answer) {
		String answerContent = (String) answer.getContent();
		try {
			List<Button> buttons = answer.getButtons();
			List<CardAction> cardActionList = getButtonCardActionList(buttons);

			HeroCard heroCard = new HeroCard();
			heroCard.setTitle("");
			heroCard.setText(answerContent);
			heroCard.setButtons(cardActionList);

			Activity reply = MessageFactory.attachment(heroCard.toAttachment());

			return reply;
		} catch (Exception e) {
			logger.error("Error parsing response", e);
			return MessageFactory.text(answerContent);
		}
	}

	private Activity createCarousel(AnswerResponse answer) {
		Object answerContent = answer.getContent();
		try {
			List<Attachment> heroCardList = new ArrayList<>();
			if(answerContent instanceof List<?>) {
				List<Object> contentList = (List<Object>) answerContent;
				Iterator<Object> contentIterator = contentList.iterator();
				while(contentIterator.hasNext()) {
					final ObjectMapper objectMapper = new ObjectMapper();
					final Content content = objectMapper.convertValue(contentIterator.next(), Content.class);

					List<CardImage> cardImageList = new ArrayList<CardImage>();
					CardImage cardImage = new CardImage();
					cardImage.setUrl(content.getImageUrl());
					cardImageList.add(cardImage);

					List<Button> buttons = content.getButtons();
					List<CardAction> cardActionList = getButtonCardActionList(buttons);					

					HeroCard heroCard = new HeroCard();
					heroCard.setTitle(content.getTitle());
					heroCard.setText(content.getSubTitle());
					heroCard.setImages(cardImageList);				
					heroCard.setButtons(cardActionList);

					heroCardList.add(heroCard.toAttachment());
				}
			} else {
				logger.error("Error creating Carousel, answer content is not an instance of List");
			}
			Activity reply = MessageFactory.carousel(heroCardList, "");
			return reply;
		} catch (Exception e) {
			logger.error("Error parsing response", e);
			return MessageFactory.text(answer.getDescription());
		}
	}

	private List<CardAction> getButtonCardActionList(List<Button> buttons) {
		Iterator<Button> buttonIterator = buttons.iterator();
		List<CardAction> cardActionList = new ArrayList<CardAction>();
		while(buttonIterator.hasNext()) {
			final Button button = buttonIterator.next();
			String buttonName = button.getName();
			ActionTypes actionType = BUTTON_TYPE_URL.equals(button.getType()) ? ActionTypes.OPEN_URL : ActionTypes.IM_BACK;
			String actionValue = BUTTON_TYPE_URL.equals(button.getType()) ? button.getAction() : button.getName();
			cardActionList.add(new CardAction() {
				{
					setTitle(buttonName);
					setType(actionType);
					setValue(actionValue);
					setText(buttonName);
				}
			});
		}
		return cardActionList;
	}
	
    public Attachment getAdaptiveCardAttachment(String fileName) {
    	logger.info("getAdaptiveCardAttachment INIT");
    	logger.info(fileName);
        try {
//            InputStream input = getClass()
//                .getClassLoader()
//                .getResourceAsStream(fileName);
//            String content = IOUtils.toString(input, StandardCharsets.UTF_8);
        	String content = fileName;
            

            return new Attachment(){{
                setContentType("application/vnd.microsoft.card.adaptive");
                setContent(new ObjectMapper().readValue(content, AdaptiveCard.class));
            }};
        } catch (IOException e) {
            LoggerFactory.getLogger(TeamsMessagingExtensionsActionPreviewBot.class)
                .error("getAdaptiveCardAttachment", e);
        }
        return new Attachment();
    }
	
	private Activity createAdaptativeCard(AnswerResponse answer) {
		logger.info("createAdaptativeCard INIT");
		logger.info(answer.getContent().toString());
		//Object answerContent = answer.getContent();
		try {
			 
			Activity reply = MessageFactory.attachment(getAdaptiveCardAttachment(answer.getContent().toString()));
			return reply;
		} catch (Exception e) {
			logger.error("Error parsing response", e);
			return MessageFactory.text(answer.getDescription());
		}
	}
}