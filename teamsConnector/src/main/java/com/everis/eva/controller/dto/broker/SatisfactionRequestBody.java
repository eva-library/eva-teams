package com.everis.eva.controller.dto.broker;

import java.io.Serializable;

public class SatisfactionRequestBody implements Serializable {
	private static final long serialVersionUID = 1L;

	private Short evaluation;
	private Boolean answered;
	private String userComments;
	private boolean expireSession;

	public SatisfactionRequestBody(Short evaluation, Boolean answered, String userComments, boolean expireSession) {
		this.evaluation = evaluation;
		this.answered = answered;
		this.userComments = userComments;
		this.expireSession = expireSession;
	}

	public Short getEvaluation() {
		return evaluation;
	}

	public Boolean getAnswered() {
		return answered;
	}

	public String getUserComments() {
		return userComments;
	}

	public boolean isExpireSession() {
		return expireSession;
	}

}
