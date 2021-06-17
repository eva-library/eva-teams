package com.everis.eva.controller.dto.broker;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;

import com.everis.eva.controller.dto.broker.answer.Button;
import com.everis.eva.controller.dto.broker.answer.QuickReply;

public class AnswerResponse implements Serializable {
	private static final long serialVersionUID = 1L;

	private Object content;

	private List<Button> buttons = new ArrayList<>();

	private List<QuickReply> quickReply = new ArrayList<>();

	private String description;

	private String type;

	private String interactionId;

	private Boolean evaluable;

	public AnswerResponse() {
		
	}
	
	private AnswerResponse(Builder builder) {
		this.content = builder.content;
		this.buttons = builder.buttons;
		this.quickReply = builder.quickReply;
		this.description = builder.description;
		this.type = builder.type;
		this.interactionId = builder.interactionId;
		this.evaluable = builder.evaluable;
	}

	public Object getContent() {
		return content;
	}

	public List<Button> getButtons() {
		return buttons;
	}

	public List<QuickReply> getQuickReply() {
		return quickReply;
	}

	public String getDescription() {
		return description;
	}

	public String getType() {
		return type;
	}

	public String getInteractionId() {
		return interactionId;
	}

	public Boolean getEvaluable() {
		return evaluable;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	public static Builder builder() {
		return new Builder();
	}

	public static final class Builder {
		private Object content;

		private List<Button> buttons = Collections.emptyList();

		private List<QuickReply> quickReply = Collections.emptyList();

		private String description;

		private String type;

		private String interactionId;

		private Boolean evaluable;

		private Builder() {
		}

		public Builder withContent(Object content) {
			this.content = content;
			return this;
		}

		public Builder withButtons(List<Button> buttons) {
			this.buttons = buttons;
			return this;
		}

		public Builder withQuickReply(List<QuickReply> quickReply) {
			this.quickReply = quickReply;
			return this;
		}

		public Builder withDescription(String description) {
			this.description = description;
			return this;
		}

		public Builder withType(String type) {
			this.type = type;
			return this;
		}

		public Builder withInteractionId(String interactionId) {
			this.interactionId = interactionId;
			return this;
		}

		public Builder withEvaluable(Boolean evaluable) {
			this.evaluable = evaluable;
			return this;
		}


		public AnswerResponse build() {
			return new AnswerResponse(this);
		}
	}

}
