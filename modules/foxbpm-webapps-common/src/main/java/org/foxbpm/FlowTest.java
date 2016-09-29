/**
 * 
 */
package org.foxbpm;

import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.foxbpm.engine.ProcessEngine;
import org.foxbpm.engine.ProcessEngineManagement;
import org.foxbpm.engine.datavariable.VariableInstance;
import org.foxbpm.engine.impl.entity.ProcessInstanceEntity;
import org.foxbpm.engine.impl.entity.VariableInstanceEntity;
import org.foxbpm.engine.runtime.ProcessInstance;
import org.foxbpm.engine.task.Task;

/**
 * @author wangzhiwei
 *
 */
public class FlowTest {

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
		
		// 获取流程引擎
		ProcessEngine engine = ProcessEngineManagement.getDefaultProcessEngine();
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
										break;
									}
								}
							}
							
							break;	//只执行一次试试
						}
					}
					
					break;	//只执行一次试试
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
