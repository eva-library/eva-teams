/*
* eVA
* Version: 3.1.0
* Copyright (C) 2020 everis Spain S.A
* Date: 01 January 2020
* Author: everis bots@everis.com
* All rights reserved
*/
package com.everis.eva;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InjectionPoint;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.converter.json.GsonHttpMessageConverter;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.client.RestTemplate;

import com.microsoft.bot.integration.AdapterWithErrorHandler;
import com.microsoft.bot.integration.BotFrameworkHttpAdapter;
import com.microsoft.bot.integration.Configuration;
import com.microsoft.bot.integration.spring.BotController;
import com.microsoft.bot.integration.spring.BotDependencyConfiguration;

@SpringBootApplication
@EnableCaching
@EnableAsync

@Import({BotController.class})

public class MsbotApplication extends BotDependencyConfiguration {

	public static void main(String[] args) {
		System.setProperty("org.slf4j.simpleLogger.logFile", "System.out");
		SpringApplication.run(MsbotApplication.class, args);
	}

	/**
     * Returns a custom Adapter that provides error handling.
     *
     * @param configuration The Configuration object to use.
     * @return An error handling BotFrameworkHttpAdapter.
     */
    @Override
    public BotFrameworkHttpAdapter getBotFrameworkHttpAdaptor(Configuration configuration) {
        return new AdapterWithErrorHandler(configuration);
    }
    
	@Bean
	public RestTemplate restTemplate() {
		RestTemplate restTemplate = new RestTemplate();
		
		restTemplate.getMessageConverters().add(new GsonHttpMessageConverter());

		return restTemplate;
	}
	
	@Bean
	public Logger getLogger(final InjectionPoint injectionPoint) {
		return LoggerFactory.getLogger(injectionPoint.getMember().getDeclaringClass());
	}
}
