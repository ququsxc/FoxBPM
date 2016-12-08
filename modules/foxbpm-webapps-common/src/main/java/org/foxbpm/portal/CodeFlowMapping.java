package org.foxbpm.portal;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.foxbpm.engine.cache.Cache;
import org.foxbpm.engine.impl.cache.DefaultCache;

public class CodeFlowMapping {
	private static final Cache<String> codeFlowCache = new DefaultCache<>();

	static {
		InputStream resourceAsStream = CodeFlowMapping.class.getResourceAsStream("/code-flow.properties");
		try {
			Properties props = new Properties();
			props.load(resourceAsStream);
			for (Object key : props.keySet()) {
				codeFlowCache.add((String) key, props.getProperty((String) key));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static String get(String code) {
		return codeFlowCache.get(code);
	}
}
