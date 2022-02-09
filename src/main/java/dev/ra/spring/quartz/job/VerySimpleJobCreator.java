package dev.ra.spring.quartz.job;

import java.util.Date;

import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.SimpleTrigger;
import org.quartz.TriggerKey;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;
import org.springframework.scheduling.quartz.SimpleTriggerFactoryBean;

public class VerySimpleJobCreator {

	private String color;
	private String jobName;
	private String jobGroup;
	private String triggerName;

	public JobDetail createJob() {

		JobDataMap jobDataMap = new JobDataMap();
		jobDataMap.put(VerySimpleJob.COLOR, color);
		jobDataMap.put(VerySimpleJob.EXECUTION_COUNT, "1");

		JobDetailFactoryBean factoryBean = new JobDetailFactoryBean();
		factoryBean.setJobClass(VerySimpleJob.class);
		factoryBean.setDurability(false);
		factoryBean.setName(jobName);
		factoryBean.setGroup(jobGroup);
		factoryBean.setJobDataMap(jobDataMap);
		factoryBean.afterPropertiesSet();
		return factoryBean.getObject();
	}

	public SimpleTrigger createSimpleTrigger(Date startTime) {

		SimpleTriggerFactoryBean factoryBean = new SimpleTriggerFactoryBean();
		factoryBean.setName(triggerName);
		factoryBean.setGroup(jobGroup);
		factoryBean.setStartTime(startTime);
		factoryBean.setRepeatCount(0);
		factoryBean.setRepeatInterval(0);
		factoryBean.afterPropertiesSet();
		return factoryBean.getObject();
	}

	public JobKey createJobKey() {
		return JobKey.jobKey(jobName, jobGroup);
	}
	
	public TriggerKey createTriggerKey() {
		return TriggerKey.triggerKey(triggerName, jobGroup);
	}

	private VerySimpleJobCreator(String color, String jobName, String triggerName, String jobGroup) {
		this.color = color;
		this.jobName = jobName;
		this.triggerName = triggerName;
		this.jobGroup = jobGroup;
	}

	public static VerySimpleJobCreator getInstance(String color) {
		String jobName = color.concat("Job");
		String jobGroup = color.concat("Group");
		String triggerName = color.concat("JobTrigger");
		return new VerySimpleJobCreator(color, jobName, triggerName, jobGroup);
	}
}
