/* eVA
* Version: 2.3.0
* copyright (c) 2018 everis Spain S.A
* Date: 01 December 2018
* Author: everis bots@everis.com - Guilherme Ferreira Gomes, Guilherme Durazzo, Caio Soliani
* All rights reserved
*/

package com.everis.eva.model;

import java.io.Serializable;

import org.apache.commons.lang3.builder.ToStringBuilder;

public class BrokerInfo implements Serializable {
	private static final long serialVersionUID = 1L;

	private String baseUrl;

	private String apiKey;

	private String project;

	private String channel;

	private String os;

	private String locale;

	public String getBaseUrl() {
		return baseUrl;
	}

	public void setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
	}

	public String getApiKey() {
		return apiKey;
	}

	public void setApiKey(String apiKey) {
		this.apiKey = apiKey;
	}

	public String getProject() {
		return project;
	}

	public void setProject(String project) {
		this.project = project;
	}

	public String getChannel() {
		return channel;
	}

	public void setChannel(String channel) {
		this.channel = channel;
	}

	public String getOs() {
		return os;
	}

	public void setOs(String os) {
		this.os = os;
	}

	public String getLocale() {
		return locale;
	}

	public void setLocale(String locale) {
		this.locale = locale;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
