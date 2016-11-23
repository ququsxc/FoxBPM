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
 * @author MAENLIANG
 */
package org.foxbpm.engine.impl.cmd;

import java.util.Map;

import org.foxbpm.engine.impl.diagramview.factory.FoxbpmProcessDefinitionVOFactory;
import org.foxbpm.engine.impl.diagramview.factory.ConcreteProcessDefinitionVOFactory;
import org.foxbpm.engine.impl.entity.ProcessDefinitionEntity;
import org.foxbpm.engine.impl.interceptor.Command;
import org.foxbpm.engine.impl.interceptor.CommandContext;
import org.foxbpm.engine.impl.util.StringUtil;

/**
 * 获取流程定义SVG图像，只显示当前用户经办的部分
 * 
 * @author yuanbing
 * @date 2016-11-21
 * 
 */
public class GetProcessDefinitionSVGForMeCmd implements Command<String> {
	private String processDefinitionId;
	private String processInstanceId;

	public GetProcessDefinitionSVGForMeCmd(String processDefinitionId, String processInstanceId) {
		this.processDefinitionId = processDefinitionId;
		this.processInstanceId = processInstanceId;
	}

	public String execute(CommandContext commandContext) {
		ProcessDefinitionEntity deployedProcessDefinition = commandContext.getProcessEngineConfigurationImpl().getDeploymentManager().findDeployedProcessDefinitionById(processDefinitionId);
		FoxbpmProcessDefinitionVOFactory svgFactory = new ConcreteProcessDefinitionVOFactory();
		String tempSVGDocument = svgFactory.createProcessDefinitionVOStringForMe(deployedProcessDefinition, processInstanceId);
		return tempSVGDocument;

	}

}
