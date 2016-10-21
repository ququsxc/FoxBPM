/**
 * 
 */
package org.foxbpm;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.foxbpm.engine.ProcessEngine;
import org.foxbpm.engine.ProcessEngineManagement;
import org.foxbpm.engine.datavariable.VariableInstance;
import org.foxbpm.engine.impl.db.SqlCommand;
import org.foxbpm.engine.impl.entity.ProcessInstanceEntity;
import org.foxbpm.engine.impl.entity.TaskEntity;
import org.foxbpm.engine.impl.entity.VariableInstanceEntity;
import org.foxbpm.engine.runtime.ProcessInstance;
import org.foxbpm.engine.task.Task;

import io.rong.RongCloud;
import io.rong.messages.TxtMessage;
import io.rong.models.CodeSuccessReslut;

/**
 * @author wangzhiwei
 *
 */
public class FlowTest {
	private final static String APPKEY = "kj7swf8o7kq22";
	private final static String APPSECRET = "spZIYiiW8OW";
	// 获取流程引擎
	private static ProcessEngine engine = ProcessEngineManagement.getDefaultProcessEngine();
	//初始化融云
	private static RongCloud rongCloud = RongCloud.getInstance(APPKEY, APPSECRET);

	/**
	 * 
	 */
	private FlowTest() {
		// TODO Auto-generated constructor stub
	}

	public static String getString(String user) {
		// TODO Auto-generated method stub
		return "Hi:" + user;
	}
	
	public static void sendMsg(ProcessInstance processInstance,
			String userId, String taskNodeId) {
		//获取当前任务处理人
		String taskUser = null;
		ProcessInstanceEntity instanceEntity = (ProcessInstanceEntity) processInstance;
		List<TaskEntity> tasks = instanceEntity.getTasks();
		for (Iterator iterator = tasks.iterator(); iterator.hasNext();) {
			TaskEntity task = (TaskEntity) iterator.next();
			if(task.getNodeId().equals(taskNodeId)) {
				taskUser = task.getAssignee();
				break;
			}
		}
		
		if(taskUser == null || taskUser.equals(userId))
			return;
		
		//这里的任务处理人必须在任务分配后才能换取，否则用上面的
//		Task task = engine.getTaskService().createTaskQuery().processInstanceId(processInstance.getId())
//				.nodeId("UserTask_3").taskNotEnd().singleResult();
//		userId = task.getAssignee();
		
		// 发送单聊消息方法（一个用户向另外一个用户发送消息，单条消息最大 128k。每分钟最多发送 6000 条信息，每次发送用户上限为 1000 人，如：一次发送 1000 人时，示为 1000 条消息。） 
//		String[] messagePublishPrivateToUserId = {taskUser};
//		TxtMessage messagePublishPrivateVoiceMessage = new TxtMessage("请尽快处理报销审批审批", "您好！请尽快处理报销审批审批");
//		CodeSuccessReslut messagePublishPrivateResult = null;
//		try {
//			messagePublishPrivateResult = rongCloud.message.publishPrivate(userId, messagePublishPrivateToUserId,
//					messagePublishPrivateVoiceMessage, "thisisapush", "{\"pushData\":\"hello\"}", "4", 0, 0, 0);
//			System.out.println("publishPrivate:  " + messagePublishPrivateResult.toString());
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
		
		// 发送讨论组消息方法（以一个用户身份向讨论组发送消息，单条消息最大 128k，每秒钟最多发送 20 条消息.） 
		TxtMessage messagePublishDiscussionTxtMessage = new TxtMessage("打雷啦，下雨收衣服啊~~", "打雷啦，下雨收衣服啊~~");
		CodeSuccessReslut messagePublishDiscussionResult = null;
		try {
			messagePublishDiscussionResult = rongCloud.message.publishDiscussion(userId, "cc1123e8-5e71-4949-b58d-4a747780deb3",
					messagePublishDiscussionTxtMessage, "thisisapush", "{\"pushData\":\"hello\"}", 1, 1);
			System.out.println("publishDiscussion:  " + messagePublishDiscussionResult.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	/**
	 * 从一个流程实例操作另一个实例的任务（根据流程变量条件）
	 * @param processInstance
	 * @return
	 */
	public static ProcessInstance getInstance(ProcessInstance processInstance) {
		// 获取当前流程实例流程变量
//		List<VariableInstanceEntity> entities = ((ProcessInstanceEntity) processInstance).getDataVariableMgmtInstance()
//				.getDataVariableEntities();
//		for (Iterator iterator = entities.iterator(); iterator.hasNext();) {
//			VariableInstanceEntity variableInstanceEntity = (VariableInstanceEntity) iterator.next();
//			variableInstanceEntity.getValueObject();
//		}
		
		// 查询实例
		List<ProcessInstance> instances = engine.getRuntimeService().createProcessInstanceQuery().list();
		if (instances != null) {
			for (Iterator iterator = instances.iterator(); iterator.hasNext();) {
				ProcessInstanceEntity processInstance2 = (ProcessInstanceEntity) iterator.next();
				
				// 根据流程定义查找
				if (processInstance2.getProcessDefinitionKey().equals("tt_1")) {
					
					// 查找所有流程变量
					List<VariableInstance> variableInstanceEntities = engine.getRuntimeService().createVariableQuery().list();
					for (Iterator iterator2 = variableInstanceEntities.iterator(); iterator2.hasNext();) {
						VariableInstanceEntity variableInstance = (VariableInstanceEntity) iterator2.next();
						
						//找到属于当前实例的流程变量
						if(variableInstance.getProcessInstanceId().equals(processInstance2.getId())	
								&& variableInstance.getKey().equals("account")
								&& "1".equals(variableInstance.getValueObject())) {
							
							// 获取实例的所有任务
							List<Task> tasks = engine.getTaskService().createTaskQuery().processInstanceId(processInstance2.getId())
									.list(); 
							if(tasks != null && tasks.size() > 0) {
								for (Iterator iterator3 = tasks.iterator(); iterator3.hasNext();) {
									Task task = (Task) iterator3.next();
									// 查找正在进行中的任务
									if (task.getEndTime() == null && task.getNodeId().equals("UserTask_3")) {
										// 停止任务
										engine.getTaskService().complete(task.getId());
//										break;
									}
								}
							}
							
						}
					}
					
				}
				
			}
		}
		
//		engine.getTaskService().createTaskQuery().list();
//		engine.getTaskService().createTaskQuery().processInstanceId(processInstance.getId()).list();
//		processInstance.getSubject();
		
		return null;
	}
	
	public static Date getMinuets(int minuet) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());

		calendar.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE) + minuet);
		return calendar.getTime();
	}
	
	public static void doSomething(String user) {
		for (int i = 0; i < 100; i++) {
			if (i == 49) {
				break;
			}
			try {
				Thread.sleep(100);
				System.out.println("========" + i);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	}
	
	//集成‘即时计算’
	public static void startFlow(ProcessInstanceEntity instance,
			Connection conn) {
		// TODO Auto-generated method stub
		for (int i = 0; i < 100; i++) {
			if (i == 49) {
				break;
			}
			try {
				Thread.sleep(100);
				System.out.println("========" + i);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("ID", "BXD-20161001053600024");
		data.put("OWNER", "admin");
		data.put("DEPT", "200011");
		data.put("ACCOUNT", "1");
		data.put("INVOICETYPE", "1");
		data.put("REASON", "不好玩");
		data.put("CREATE_TIME", "2016-10-11");
		data.put("PROCESSINSTANCEID", instance.getId());
		
		SqlCommand command = new SqlCommand(conn);
		command.insert("TB_EXPENSE", data);
		command.commit();
		
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		//设置bizKey
		instance.setBizKey("BXD-20161001053600024");
	}
	
	public static void main(String[] args) {
//		Date date = new Date();
//		date.getTime();
//		System.out.println(date);

//		Calendar calendar = Calendar.getInstance();
//		calendar.setTime(new Date());
//		System.out.println(calendar.get(Calendar.DAY_OF_MONTH));// 今天的日期
//		calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) + 1);// 让日期加1
//		System.out.println(calendar.get(Calendar.DATE));// 加1之后的日期Top
//		
//		calendar.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE) + 1);// 让日期加1
//		calendar.get(Calendar.MINUTE);
//		calendar.getTime();
		
	}
}
