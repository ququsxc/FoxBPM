/**
 * Copyright 1996-2014 FoxBPM ORG.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * @author yangguangftlp
 */
package org.foxbpm.engine.impl.cmd;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.foxbpm.engine.datavariable.BizDataObjectBehavior;
import org.foxbpm.engine.datavariable.DataObjectDefinition;
import org.foxbpm.engine.impl.ProcessEngineConfigurationImpl;
import org.foxbpm.engine.impl.datavariable.BizDataObject;
import org.foxbpm.engine.impl.datavariable.DataObjectDefinitionImpl;
import org.foxbpm.engine.impl.interceptor.Command;
import org.foxbpm.engine.impl.interceptor.CommandContext;
import org.foxbpm.engine.impl.util.ExceptionUtil;
import org.foxbpm.engine.impl.util.ReflectUtil;
import org.foxbpm.engine.impl.util.StringUtil;

/**
 * 获取业务数据对象命令类
 * 
 * @author yangguangftlp
 * @date 2014年7月26日
 */
public class GetBizDataObjectCmd implements Command<List<BizDataObject>> {
	
	protected String behaviorId;
	protected String dataSource;
	
	public GetBizDataObjectCmd(String behaviorId, String dataSource) {
		this.behaviorId = behaviorId;
		this.dataSource = dataSource;
	}
	
	 
	@SuppressWarnings("unchecked")
	public List<BizDataObject> execute(CommandContext commandContext) {
		
		if (StringUtil.isEmpty(behaviorId)) {
			throw ExceptionUtil.getException("00001003");
		}
		ProcessEngineConfigurationImpl processEngine = commandContext.getProcessEngineConfigurationImpl();
		List<DataObjectDefinitionImpl> dataObjBehaviorList = processEngine.getFoxBpmConfig().getDataObjectDefinitions();
		if (null == dataObjBehaviorList) {
			return Collections.EMPTY_LIST;
		}
		
		DataObjectDefinition dataObjectBehavior = null;
		for (Iterator<DataObjectDefinitionImpl> iterator = dataObjBehaviorList.iterator(); iterator.hasNext();) {
			dataObjectBehavior = (DataObjectDefinition) iterator.next();
			if (behaviorId.equals(dataObjectBehavior.getId())) {
				break;
			}
		}
		
		// 判断是否存在type对应的数据处理行为
		if (null == dataObjectBehavior) {
			return Collections.EMPTY_LIST;
		}
		
		// 实例化业务数据对象行为处理类
		BizDataObjectBehavior bizDataObjectBehavior = null;
		try{
			bizDataObjectBehavior = (BizDataObjectBehavior)ReflectUtil.instantiate(StringUtil.trim(dataObjectBehavior.getBehavior()));
		}catch(Exception ex){
			throw ExceptionUtil.getException("00005003",ex,behaviorId);
		}
		return bizDataObjectBehavior.getDataObjects(dataSource);
	}
}
