package com.everis.eva.controller.dto.broker;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.springframework.util.CollectionUtils;


public class ConversationResponse implements Serializable {
	private static final long serialVersionUID = 1L;

	private String text;

	private String sessionCode;

	private String intent;

	private Double confidence;

	private List<AnswerResponse> answers = new ArrayList<>();

	private Map<String, Object> context = new HashMap<>();

	private Map<String, Object> contextReadOnly = new HashMap<>();

	public ConversationResponse() {		
	}
	
	public ConversationResponse(String sessionCode, ConversationRequestBody conversationRequestBodyDTO) {
		this.sessionCode = sessionCode;
		this.text = conversationRequestBodyDTO.getUserInput();
		this.putAllContext(conversationRequestBodyDTO.getContext());
	}

	public ConversationResponse(String sessionCode, String text) {
		this.sessionCode = sessionCode;
		this.text = text;
	}

	public String getText() {
		return text;
	}

	public String getSessionCode() {
		return sessionCode;
	}

	public String getIntent() {
		return intent;
	}

	public Double getConfidence() {
		return confidence;
	}

	public List<AnswerResponse> getAnswers() {
		return Collections.unmodifiableList(answers);
	}

	public Map<String, Object> getContext() {
		return Collections.unmodifiableMap(context);
	}

	public Map<String, Object> getContextReadOnly() {
		return Collections.unmodifiableMap(contextReadOnly);
	}

	public void setIntent(String intent) {
		this.intent = intent;
	}

	public void setConfidence(Double confidence) {
		this.confidence = confidence;
	}

	public void setText(String text) {
		this.text = text;
	}

	public void setSessionCode(String sessionCode) {
		this.sessionCode = sessionCode;
	}

	public void setAnswers(List<AnswerResponse> answers) {
		this.answers = answers;
	}

	public void setContext(Map<String, Object> context) {
		this.context = context;
	}

	public void setContextReadOnly(Map<String, Object> contextReadOnly) {
		this.contextReadOnly = contextReadOnly;
	}

	public void addAllAnswer(List<AnswerResponse> answers) {
		this.answers.addAll(answers);
	}

	public void addContextReadOnly(String key, Object value) {
		this.contextReadOnly.put(key, value);
	}

	public void removeContextReadOnly(String key) {
		this.contextReadOnly.remove(key);
	}

	public void putAllContext(Map<String, Object> context) {
		if(!CollectionUtils.isEmpty(context)) {
			this.context.putAll(context);
		}
	}

	public void putAllContextReadOnly(Map<String, Object> contextReadOnly) {
		if(!CollectionUtils.isEmpty(contextReadOnly)) {
			this.contextReadOnly.putAll(contextReadOnly);
		}
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	public void clearAnswers() {
		if(answers != null){
			answers.clear();
		}
	}

	public void clearContext() {
		if(context != null){
			context.clear();
		}
	}

	public void replaceVisibleContext(Map<String, Object> contextReadOnly) {
		if(contextReadOnly != null) {
			this.contextReadOnly  = contextReadOnly;
		}
	}
	
}
