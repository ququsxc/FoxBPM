/**
 * 
 */
package org.foxbpm.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
	
	public List<Map<String, Object>> getInstanceBiz() {
		List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("userId", "1001");
		map.put("userName", "张值班");
		
		list.add(map);
		
		map = new HashMap<String, Object>();
		map.put("userId", "1002");
		map.put("userName", "李管理");
		
		list.add(map);
		
		map = new HashMap<String, Object>();
		map.put("userId", "admin");
		map.put("userName", "超级管理员");
		
		list.add(map);
		
		return list;
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
