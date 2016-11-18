package org.foxbpm.common;

import org.foxbpm.engine.cache.Cache;
import org.foxbpm.engine.impl.cache.DefaultCache;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Constants {
	public static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

	public static final Cache<String> LOGIN_TOKEN_CACHE = new DefaultCache<>();

}
