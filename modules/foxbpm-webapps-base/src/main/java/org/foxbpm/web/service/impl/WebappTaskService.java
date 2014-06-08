package org.foxbpm.web.service.impl;

import java.util.List;

import lombok.Setter;

import org.foxbpm.engine.TaskService;
import org.foxbpm.engine.task.Task;
import org.foxbpm.web.db.factory.FoxbpmDBConnectionFactory;
import org.foxbpm.web.db.interfaces.BizDBInterface;
import org.foxbpm.web.service.interfaces.IWebappTaskService;

/**
 * 任务服务类
 * 
 * @author MEL
 * @date 2014-06-04
 */
public class WebappTaskService implements IWebappTaskService {
	@Setter
	private BizDBInterface bizDB;
	@Setter
	private FoxbpmDBConnectionFactory dbfactory;
	@Setter
	TaskService taskService;

	@Override
	public void completeTask(String taskId) {
		// 开启数据库连接
		// 启动事务，事务由spring控制
		// 处理业务逻辑数据
		taskService.complete(taskId);
	}

	@Override
	public List<Task> queryTask() {
		return taskService.createTaskQuery().list();
	}

}
