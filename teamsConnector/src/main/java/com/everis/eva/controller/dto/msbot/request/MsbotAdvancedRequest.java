/*
* eVA
* Version: 3.1.0
* Copyright (C) 2020 everis Spain S.A
* Date: 01 January 2020
* Author: everis bots@everis.com
* All rights reserved
*/
package com.everis.eva.controller.dto.msbot.request;

import com.google.gson.annotations.SerializedName;

public class MsbotAdvancedRequest {

	@SerializedName("text")
	private String text;
	
	@SerializedName("chatId")
	private Long chatId;

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public Long getChatId() {
		return chatId;
	}

	public void setChatId(Long chatId) {
		this.chatId = chatId;
	}

	@Override
	public String toString() {
		return "MsbotAdvancedRequest [text=" + text + ", chatId=" + chatId + "]";
	}

}