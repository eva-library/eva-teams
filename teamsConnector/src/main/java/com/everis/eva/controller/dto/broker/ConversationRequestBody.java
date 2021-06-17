package com.everis.eva.controller.dto.broker;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

public class ConversationRequestBody implements Serializable {
	private static final long serialVersionUID = 1L;

	private String text;

	private String code;

	private Map<String, Object> context = new HashMap<>();

	public ConversationRequestBody(String text, String code, Map<String, Object> context) {
		this.text = text;
		this.code = code;
		this.putAllContext(context);
	}

	public String getText() {
		return text;
	}

	public String getCode() {
		return code;
	}

	public Map<String, Object> getContext() {
		return Collections.unmodifiableMap(context);
	}

	public void setText(String text) {
		this.text = text;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public void putAllContext(Map<String, Object> context) {
		if(!CollectionUtils.isEmpty(context)) {
			this.context.putAll(context);
		}
	}

	public String getUserInput() {
		return StringUtils.isEmpty(this.getText()) ? this.getCode() : this.getText();
	}

}