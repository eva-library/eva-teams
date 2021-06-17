/*
* eVA
* Version: 3.1.0
* Copyright (C) 2020 everis Spain S.A
* Date: 01 January 2020
* Author: everis bots@everis.com
* All rights reserved
*/
package com.everis.eva.service.cache;

import java.util.Map;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import static com.everis.eva.Constants.CACHE_MS_BOT_SERVICE_ID;

@Service
public class MsbotCache {

	@Autowired
	private Logger logger;

	@Cacheable(value = CACHE_MS_BOT_SERVICE_ID, key = "#phoneNumber")
	public Map<String, Object> getUserKeys(String phoneNumber) {
		return null;
	}

	@CachePut(value = CACHE_MS_BOT_SERVICE_ID, key = "#phoneNumber")
	public Map<String, Object> setUserKeys(String phoneNumber, Map<String, Object> keys) {
		return keys;
	}

	@CacheEvict(value = CACHE_MS_BOT_SERVICE_ID, key = "#phoneNumber", allEntries = true)
	public void removeUserKeysFromCache(String phoneNumber) {
		logger.info("clearContextCache invoked");
	}

	@CacheEvict(value = CACHE_MS_BOT_SERVICE_ID, allEntries = true)
	public void clearContextCache() {
		logger.info("clearContextCache invoked");
	}

}
