/*
* eVA
* Version: 3.1.0
* Copyright (C) 2020 everis Spain S.A
* Date: 01 January 2020
* Author: everis bots@everis.com
* All rights reserved
*/
package com.everis.eva.controller.dto.msbot.request;

import javax.xml.bind.annotation.XmlType;

@XmlType
public class MsbotRequest {

	private String text;
	private String chatId;

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getChatId() {
		return chatId;
	}

	public void setChatId(String chatId) {
		this.chatId = chatId;
	}

	@Override
	public String toString() {
		return "MsbotRequest [text=" + text + "]";
	}
}
