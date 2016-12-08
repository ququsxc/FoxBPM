package org.foxbpm.portal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.foxbpm.common.RestResult;
import org.foxbpm.disclosure.dao.DisclosureDao;
import org.foxbpm.disclosure.model.Disclosure;
import org.foxbpm.engine.ProcessEngine;
import org.foxbpm.engine.ProcessEngineManagement;
import org.foxbpm.engine.impl.entity.ProcessInstanceEntity;
import org.foxbpm.engine.impl.entity.UserEntity;
import org.foxbpm.engine.impl.identity.Authentication;
import org.foxbpm.engine.runtime.ProcessInstance;
import org.foxbpm.engine.task.Task;
import org.foxbpm.rest.pojo.Group;
import org.foxbpm.rest.pojo.User;
import org.springframework.web.client.RestTemplate;

import io.rong.RongCloud;
import io.rong.messages.TxtMessage;
import io.rong.models.CodeSuccessReslut;

public class ScriptHandler {
	private final static String APPKEY = "kj7swf8o7kq22";
	private final static String APPSECRET = "spZIYiiW8OW";
	// 获取流程引擎
	private static ProcessEngine engine = ProcessEngineManagement.getDefaultProcessEngine();
	// 初始化融云
	private static RongCloud rongCloud = RongCloud.getInstance(APPKEY, APPSECRET);

	private static final List<String> USER_IDS = new ArrayList<>();
	private static final List<User> IM_USERS = new ArrayList<>();
	private static final Group IM_GROUP = new Group("zhuangxiu", "装修群", "http://pic.qiantucdn.com/10/37/86/49bOOOPIC65.jpg");
	private static boolean IM_GROUP_CREATE_FLAG = false;
	private static final User IM_SYSTEM_USER = new User("sys", "管理员", "https://ss0.bdstatic.com/70cFuHSh_Q1YnxGkpoWK1HF6hhy/it/u=3594851097,256030855&fm=116&gp=0.jpg");

	public static List<User> userList() {
		return IM_USERS;
	}

	public static List<Group> groupList() {
		return Arrays.asList(IM_GROUP);
	}

	public static void prepareDisclosureData(ProcessInstanceEntity instance) throws Exception {
		USER_IDS.clear();
		String designerId = null;
		String supervisorId = "zjl";
		String ownerId = "zyz";
		String workerId = "wsf";
		boolean hasDesigner = designerId != null;
		boolean hasSupervisor = supervisorId != null;

		USER_IDS.add(ownerId);
		USER_IDS.add(workerId);

		DisclosureDao dao = SpringContextHolder.getBean(DisclosureDao.class);
		Disclosure bean = new Disclosure();
		String id = "JD-" + DateFormatUtils.format(new Date(), "yyyyMMddhhmmsssss");
		bean.setId(id);
		bean.setOwnerId("zyz");
		bean.setOwnerContact("15011111111");
		bean.setWorkerId("wsf");
		bean.setWorkerContact("15022222222");
		if (hasDesigner) {
			bean.setDesignerId(designerId);
			bean.setDesignerContact("15033333333");
			USER_IDS.add(designerId);
		}
		if (hasSupervisor) {
			bean.setSupervisorId(supervisorId);
			bean.setSupervisorContact("15044444444");
			USER_IDS.add(supervisorId);
		}
		bean.setHouseType("一室一厅");
		bean.setHouseAddress("工业园区新平街莲花家园2#1208");
		bean.setHouseArea("118平米");
		bean.setDecorationCompany("家装E站");
		bean.setDecorationStyle("地中海");
		bean.setDecorationType("全包");
		bean.setHousePicture("xxx");

		dao.save(bean);

		instance.setBizKey(id);
		Map<String, Object> vars = new HashMap<>();
		vars.put("hasDesigner", hasDesigner);
		vars.put("hasSupervisor", hasSupervisor);
		vars.put("users", USER_IDS);
		instance.setVariables(vars);

		if (!IM_GROUP_CREATE_FLAG) {
			IM_USERS.add(IM_SYSTEM_USER);
			IM_GROUP_CREATE_FLAG = true;
			for (String userId : USER_IDS) {
				UserEntity user = Authentication.selectUserByUserId(userId);
				IM_USERS.add(new User(userId, user.getUserName(), user.getImage()));
			}

			rongCloud.group.dismiss(USER_IDS.get(0), IM_GROUP.getGroupId());
			rongCloud.group.create(USER_IDS.toArray(new String[] {}), IM_GROUP.getGroupId(), IM_GROUP.getGroupName());
		}

	}

	public static void sendGroupMsg(String message) throws Exception {
		CodeSuccessReslut result = rongCloud.message.publishGroup(IM_SYSTEM_USER.getUserId(), new String[] { IM_GROUP.getGroupId() }, new TxtMessage(message, null), null, null, null, null);
		System.out.println("发送群消息: " + result.toString());
	}

	public static void sendSystemMsg(String message, String... toUser) throws Exception {
		if (toUser == null || toUser.length == 0) {
			toUser = USER_IDS.toArray(new String[] {});
		}
		CodeSuccessReslut result = rongCloud.message.PublishSystem(IM_SYSTEM_USER.getUserId(), toUser, new TxtMessage(message, null), null, null, null, null);
		System.out.println("发送系统消息: " + result.toString());
	}

}
