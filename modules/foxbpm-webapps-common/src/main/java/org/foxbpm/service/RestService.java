/**
 * 
 */
package org.foxbpm.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
		map.put("userList", Arrays.asList(new User("1001", "张值班", "https://ss0.bdstatic.com/70cFuHSh_Q1YnxGkpoWK1HF6hhy/it/u=3660411511,513290481&fm=116&gp=0.jpg"), new User("1002", "李管理", "https://ss0.bdstatic.com/70cFvHSh_Q1YnxGkpoWK1HF6hhy/it/u=3461331753,1116232261&fm=116&gp=0.jpg"), new User("admin", "超级管理员", "https://ss0.bdstatic.com/70cFvHSh_Q1YnxGkpoWK1HF6hhy/it/u=567877910,3578077276&fm=116&gp=0.jpg")));
		map.put("groupList", Arrays.asList(new Group("group_test", "测试群", "https://ss1.bdstatic.com/70cFuXSh_Q1YnxGkpoWK1HF6hhy/it/u=2079126539,1565673360&fm=116&gp=0.jpg")));

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
