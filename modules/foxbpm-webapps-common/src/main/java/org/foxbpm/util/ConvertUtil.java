package org.foxbpm.util;

import java.util.HashMap;
import java.util.Map;

import org.springframework.cglib.beans.BeanMap;

public class ConvertUtil {
	public static <T> Map<String, Object> beanToMap(T bean) {
		Map<String, Object> map = new HashMap<>();
		if (bean != null) {
			BeanMap beanMap = BeanMap.create(bean);
			for (Object key : beanMap.keySet()) {
				if (beanMap.get(key) != null) {
					map.put(String.valueOf(key), beanMap.get(key));
				}
			}
		}
		return map;
	}
}
