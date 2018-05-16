package org.openmrs.module.xdssender.api.scheduler.impl;

import org.openmrs.api.context.Context;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.xdssender.XdsSenderConfig;
import org.openmrs.module.xdssender.api.scheduler.PullNotificationsTask;
import org.openmrs.module.xdssender.api.scheduler.XdsSenderSchedulerService;
import org.openmrs.scheduler.SchedulerException;
import org.openmrs.scheduler.SchedulerService;
import org.openmrs.scheduler.TaskDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;

@Service
public class XdsSenderSchedulerServiceImpl extends BaseOpenmrsService implements XdsSenderSchedulerService {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(XdsSenderSchedulerServiceImpl.class);
	
	@Autowired
	private XdsSenderConfig config;
	
	@Autowired
	private SchedulerService schedulerService;
	
	@Override
	public void runXdsSenderScheduler() {
		schedulePullNotificationsTask();
	}
	
	private void schedulePullNotificationsTask() {
		TaskDefinition task = createTask(PullNotificationsTask.TASK_NAME, PullNotificationsTask.TASK_DESCRIPTION,
		    PullNotificationsTask.class.getName(), config.getPullNotificationsTaskInterval());
		
		scheduleTask(task);
	}
	
	private void scheduleTask(TaskDefinition taskDefinition) {
		try {
			if (schedulerService.getScheduledTasks().contains(taskDefinition)) {
				schedulerService.rescheduleTask(taskDefinition);
			} else {
				schedulerService.scheduleTask(taskDefinition);
			}
		}
		catch (SchedulerException e) {
			LOGGER.error("Error during starting " + taskDefinition.getName() + ":", e);
		}
	}
	
	private TaskDefinition createTask(String name, String description, String taskClass, Long interval) {
		TaskDefinition result = schedulerService.getTaskByName(name);
		
		if (result == null) {
			result = new TaskDefinition();
		}
		
		result.setName(name);
		result.setDescription(description);
		result.setTaskClass(taskClass);
		result.setRepeatInterval(interval);
		result.setStartTime(new Timestamp(System.currentTimeMillis()));
		result.setStartOnStartup(true);
		
		try {
			Context.getSchedulerService().saveTaskDefinition(result);
		}
		catch (Exception e) {
			LOGGER.error("Error during save " + name + " definition: ", e);
		}
		
		return schedulerService.getTaskByName(name);
	}
}
