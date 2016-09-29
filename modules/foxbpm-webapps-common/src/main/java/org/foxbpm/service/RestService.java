/**
 * 
 */
package org.foxbpm.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

/**
 * @author wangzhiwei
 *
 */
@Service
public class RestService {
	public Map<String, String> getInstanceBiz() {
		Map<String, String> map = new HashMap<String, String>();
		map.put("expenseId", "BXD-0000000000000");
		map.put("createTime", "2018-08-08");
		map.put("ownerName", "马蓉");
		map.put("owner", "bitch");
		map.put("dept", "1");
		map.put("account", "3");
		
		return map;
	}
}
