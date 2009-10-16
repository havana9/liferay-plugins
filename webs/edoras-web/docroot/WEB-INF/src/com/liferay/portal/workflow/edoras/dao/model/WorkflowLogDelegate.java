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
import com.liferay.portal.workflow.edoras.model.WorkflowLog;
import com.liferay.portal.workflow.edoras.model.impl.WorkflowLogImpl;

import org.edorasframework.process.api.ProcessSystemUtil;
import org.edorasframework.process.api.model.ProcessModel;
import org.edorasframework.process.api.session.ProcessSession;
import org.edorasframework.process.core.log.model.AbstractProcessLog;
import org.springframework.util.Assert;


/**
 * <a href="WorkflowLogBridge.java.html"><b><i>View Source</i></b></a>
 *
 * @author Micha Kiener
 */
public class WorkflowLogDelegate<T extends AbstractProcessLog>
	implements WorkflowEntity {


	public long getPrimaryKey() {
		return _workflowLog.getPrimaryKey();
	}

	public WorkflowLog initializeForInsert(T workflowLog) {
		unwrap();
		transferPropertiesForSaving(workflowLog);
		return _workflowLog;
	}
	
	public WorkflowLog initializeForUpdate(T workflowLog) {
		unwrap();
		transferPropertiesForSaving(workflowLog);
		return _workflowLog;
	}

	public void initializeFromReading(WorkflowLog workflowLogSource, T workflowLogTarget) {
		_workflowLog = workflowLogSource;

		ProcessSession processSession = ProcessSystemUtil.getCurrentSession();
		Assert.notNull(
			processSession,
			"No process session while reading workflow entities.");

		workflowLogTarget.setId(workflowLogSource.getPrimaryKey());
		workflowLogTarget.setTenantId(workflowLogSource.getCompanyId());
		workflowLogTarget.setCreationTime(workflowLogSource.getCreateDate());
		workflowLogTarget.setCreationUser(workflowLogSource.getUserName());

		WorkflowInstance workflowInstance = workflowLogSource.getWorkflowInstance();
		WorkflowInstanceBridge workflowInstanceBridge =
			new WorkflowInstanceBridge(workflowInstance);
		workflowLogTarget.setProcessInstance(workflowInstanceBridge);

		ProcessModel processModel =
			processSession.getService().getProcessModel(
				workflowLogSource.getWorkflowDefinitionId());
		workflowLogTarget.setProcessModel(processModel);
		workflowLogTarget.setProcessId(processModel.getProcessModelId());
		workflowLogTarget.setProcessVersion(processModel.getProcessModelVersion());
		
		workflowLogTarget.setInformation(workflowLogSource.getDescription());
	}

	public boolean setNew(boolean isNew) {
		WorkflowLog workflowLog = unwrap();
		
		return workflowLog.setNew(isNew);
	}

	public void setPrimaryKey(long primaryKey) {
		_workflowLog.setPrimaryKey(primaryKey);
	}

	public void transferPropertiesForSaving(T workflowLog) {
		_workflowLog.setLogEntityType(workflowLog.getType().getId());
		_workflowLog.setPrimaryKey(workflowLog.getId());
		_workflowLog.setCompanyId(workflowLog.getTenantId());
		_workflowLog.setCreateDate(workflowLog.getCreationTime());
		_workflowLog.setUserName(workflowLog.getCreationUser());

		_workflowLog.setWorkflowInstanceId(
			workflowLog.getProcessInstance().getPrimaryKey());

		_workflowLog.setWorkflowDefinitionId(
			workflowLog.getProcessModel().getRepositoryPK());

		_workflowLog.setDescription(workflowLog.getInformation());
	}

	public WorkflowLog unwrap() {
		if (_workflowLog == null) {
			_workflowLog = new WorkflowLogImpl();
		}

		return _workflowLog;
	}

	private WorkflowLog _workflowLog;
}
