/**
 * 
 */
package org.foxbpm.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import io.rong.RongCloud;
import io.rong.models.TokenReslut;

/**
 * @author wangzhiwei
 *
 */
@Service
public class RestService {
	private final String APPKEY = "kj7swf8o7kq22";
	private final String APPSECRET = "spZIYiiW8OW";
	
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
	
	/**
	 * 获取用户token
	 * @param userId
	 * @param userName
	 * @param image
	 * @return
	 * @throws Exception
	 */
	public TokenReslut getUserToken(String userId, String userName, String image) throws Exception {
		RongCloud rongCloud = RongCloud.getInstance(APPKEY, APPSECRET);

		// 获取 Token 方法
		TokenReslut userGetTokenResult = rongCloud.user.getToken(userId, userName, image);
		System.out.println("getToken:  " + userGetTokenResult.toString());

		return userGetTokenResult;
	}
}
