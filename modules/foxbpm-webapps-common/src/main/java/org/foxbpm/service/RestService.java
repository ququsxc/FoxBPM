/**
 * 
 */
package org.foxbpm.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.foxbpm.engine.impl.entity.UserEntity;
import org.foxbpm.engine.impl.identity.Authentication;
import org.foxbpm.portal.ScriptHandler;
import org.foxbpm.rest.pojo.Group;
import org.foxbpm.rest.pojo.User;
import org.springframework.stereotype.Service;

import io.rong.RongCloud;
import io.rong.messages.TxtMessage;
import io.rong.models.CodeSuccessReslut;
import io.rong.models.GroupUserQueryReslut;
import io.rong.models.TokenReslut;

/**
 * @author wangzhiwei
 *
 */
@Service
public class RestService {
	private final String APPKEY = "kj7swf8o7kq22";
	private final String APPSECRET = "spZIYiiW8OW";

	public Map<String, Object> data() {

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("userList", ScriptHandler.userList());
		map.put("groupList", ScriptHandler.groupList());

		return map;
	}

	/**
	 * 获取用户token
	 * 
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

	public void createGroup() throws Exception {
		RongCloud rongCloud = RongCloud.getInstance(APPKEY, APPSECRET);
		String[] groupCreateUserId = { "1001", "1002", "admin" };
		CodeSuccessReslut groupCreateResult = rongCloud.group.create(groupCreateUserId, "group_test", "测试群");
		System.out.println("create:  " + groupCreateResult.toString());
	}

	public void sendGroupMessage() throws Exception {
		RongCloud rongCloud = RongCloud.getInstance(APPKEY, APPSECRET);
		String[] messagePublishGroupToGroupId = { "group_test" };
		TxtMessage messagePublishGroupTxtMessage = new TxtMessage("hello!!!!!!", null);
		CodeSuccessReslut messagePublishGroupResult = rongCloud.message.publishGroup("1001", messagePublishGroupToGroupId, messagePublishGroupTxtMessage, null, null, null, null);
		System.out.println("publishGroup:  " + messagePublishGroupResult.toString());
	}

	public GroupUserQueryReslut getGroupUsers(String groupId) throws Exception {
		RongCloud rongCloud = RongCloud.getInstance(APPKEY, APPSECRET);
		GroupUserQueryReslut result = rongCloud.group.queryUser(groupId);
		System.out.println("create:  " + result.toString());
		return result;
	}
}
