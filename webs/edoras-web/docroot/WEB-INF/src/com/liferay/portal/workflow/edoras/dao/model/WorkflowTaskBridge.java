/**
 * Copyright (c) 2000-2009 Liferay, Inc. All rights reserved.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.liferay.portal.workflow.edoras.dao.model;

import com.liferay.portal.workflow.edoras.model.WorkflowInstance;
import com.liferay.portal.workflow.edoras.model.WorkflowTask;
import com.liferay.portal.workflow.edoras.model.impl.WorkflowTaskImpl;

import org.edorasframework.process.api.ProcessSystemUtil;
import org.edorasframework.process.api.entity.ProcessInstance;
import org.edorasframework.process.api.model.ProcessModel;
import org.edorasframework.process.api.service.ProcessService;
import org.edorasframework.process.api.session.ProcessSession;
import org.edorasframework.process.api.task.TaskPriority;
import org.edorasframework.process.workflow.api.TaskState;
import org.edorasframework.process.workflow.task.DefaultWorkflowTask;
import org.springframework.util.Assert;

/**
 * <a href="WorkflowTaskBridge.java.html"><b><i>View Source</i></b></a>
 *
 * @author Micha Kiener
 */
public class WorkflowTaskBridge extends DefaultWorkflowTask
	implements WorkflowEntity, WorkflowEntityBridge<WorkflowTask> {

	public WorkflowTaskBridge() {

	}

	public WorkflowTaskBridge(WorkflowTask workflowTask) {
		initializeFromReading(workflowTask);
	}

	public long getWorkflowDefinitionId() {
		if (_worflowDefinitionId != 0) {
			return _worflowDefinitionId;
		}

		ProcessSession processSession = ProcessSystemUtil.getCurrentSession();

		ProcessService processService = processSession.getService();

		ProcessModel processModel =
			processService.getProcessModel(
				getProcessModelId(), getProcessModelVersion());

		_worflowDefinitionId = processModel.getRepositoryPK();

		return _worflowDefinitionId;
	}
	
	public WorkflowTask initializeForInsert() {
		unwrap();
		transferPropertiesForSaving();
		return _workflowTask;
	}

	public WorkflowTask initializeForUpdate() {
		transferPropertiesForSaving();
		return _workflowTask;
	}

	public boolean setNew(boolean isNew) {
		WorkflowTask workflowTask = unwrap();

		return workflowTask.setNew(isNew);
	}

	public void initializeFromReading(WorkflowTask workflowTask) {
		_workflowTask = workflowTask;

		ProcessSession processSession = ProcessSystemUtil.getCurrentSession();
		Assert.notNull(
			processSession,
			"No process session while reading workflow entities.");
		
		setId(workflowTask.getPrimaryKey());
		setTenantId(workflowTask.getCompanyId());
		setCreationDate(workflowTask.getCreateDate());
		setDueDate(workflowTask.getDueDate());
		setCompletionDate(workflowTask.getCompletionDate());
		setCompleted(workflowTask.getCompleted());
		
		WorkflowInstance workflowInstance = workflowTask.getWorkflowInstance();
		WorkflowInstanceBridge workflowInstanceBridge =
			new WorkflowInstanceBridge(workflowInstance);
		setProcessInstance(workflowInstanceBridge);

		setMetaName(workflowTask.getMetaName());
		setRelation(workflowTask.getRelation());

		ProcessModel processModel =
			processSession.getService().getProcessModel(
				workflowTask.getWorkflowDefinitionId());

		setProcessModelId(processModel.getProcessModelId());
		setProcessModelVersion(processModel.getProcessModelVersion());
		
		setTaskId(workflowTask.getFriendlyId());
		setAssignee(workflowTask.getAssigneeUserName());
		setPriority(TaskPriority.getPriority(workflowTask.getPriority()));
		
		setState(TaskState.getState(workflowTask.getState()));
		setAssignedGroup(workflowTask.getAssignedGroup());
		
		postLoad();
	}

	public void transferPropertiesForSaving() {
		unwrap();
		_workflowTask.setPrimaryKey(getPrimaryKey());
		_workflowTask.setCompanyId(getTenantId());
		_workflowTask.setCreateDate(getCreationDate());
		_workflowTask.setDueDate(getDueDate());
		_workflowTask.setCompletionDate(getCompletionDate());
		_workflowTask.setCompleted(isCompleted());
		
		ProcessInstance processInstance = getProcessInstance();
		_workflowTask.setWorkflowInstanceId(processInstance.getPrimaryKey());

		_workflowTask.setMetaName(getMetaName());
		_workflowTask.setRelation(getRelation());

		_workflowTask.setWorkflowDefinitionId(getWorkflowDefinitionId());

		_workflowTask.setFriendlyId(getTaskId());
		_workflowTask.setAssigneeUserName(getAssignee());
		_workflowTask.setAssignedGroup(getAssignedGroup());
		_workflowTask.setPriority(getPriority().getPriority());
		_workflowTask.setState(getState().getState());
	}

	public WorkflowTask unwrap() {
		if (_workflowTask == null) {
			_workflowTask = new WorkflowTaskImpl();
		}

		return _workflowTask;
	}

	private transient long _worflowDefinitionId;
	private transient WorkflowTask _workflowTask;
}