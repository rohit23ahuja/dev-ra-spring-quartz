package dev.ra.spring.quartz.controller;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;
import org.quartz.impl.matchers.GroupMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dev.ra.spring.quartz.SpringQuartzExample;
import dev.ra.spring.quartz.job.VerySimpleJobCreator;
import dev.ra.spring.quartz.request.DeleteJobRequest;
import dev.ra.spring.quartz.request.ScheduleJobRequest;
import dev.ra.spring.quartz.response.ScheduledJob;

@RestController
@RequestMapping("/api/job")
public class JobController {
	private static Logger _log = LoggerFactory.getLogger(SpringQuartzExample.class);

	@Autowired
	private SchedulerFactoryBean schedulerFactoryBean;

	@PostMapping("/")
	public String scheduleJob(@RequestBody ScheduleJobRequest scheduleJobRequest) throws Exception {
		Scheduler scheduler = schedulerFactoryBean.getScheduler();

		String color = scheduleJobRequest.getColor();
		VerySimpleJobCreator verySimpleJobCreator = VerySimpleJobCreator.getInstance(color);

		JobKey jobKey = verySimpleJobCreator.createJobKey();
		if (!scheduler.checkExists(jobKey)) {
			Instant instant = Instant.parse(scheduleJobRequest.getRunTime());
			Date runTime = Date.from(instant);

			_log.info("Job|Scheduling");
			JobDetail jobDetail = verySimpleJobCreator.createJob();
			SimpleTrigger trigger = verySimpleJobCreator.createSimpleTrigger(runTime);

//			_log.info("Scheduler|Registering Listener");
//			Matcher<JobKey> matcher = KeyMatcher.keyEquals(jobDetail.getKey());
//			scheduler.getListenerManager().addJobListener(verySimpleJobListener, matcher);

			Date scheduledTime = scheduler.scheduleJob(jobDetail, trigger);
			_log.info("Job|Scheduled| Key {} Time {}", jobDetail.getKey(), scheduledTime);
		}
		return "Job Scheduled";
	}

	@DeleteMapping("/")
	public String deleteJob(@RequestBody DeleteJobRequest deleteJobRequest) throws Exception {
		if (deleteJobRequest.getColor() != null) {
			VerySimpleJobCreator verySimpleJobCreator = VerySimpleJobCreator.getInstance(deleteJobRequest.getColor());
			Scheduler scheduler = schedulerFactoryBean.getScheduler();
			JobKey jobKey = verySimpleJobCreator.createJobKey();
			boolean result = scheduler.deleteJob(jobKey);
			if (result) {
				String response = String.format("Job Deleted, Key %s", jobKey);
				_log.info("Job|Deletion| {}", response);
				return response;
			} else {
				throw new Exception("No such job exists");
			}
		} else if (deleteJobRequest.getEmail() != null) {
			Scheduler scheduler = schedulerFactoryBean.getScheduler();
			scheduler.clear();
			String response = "Jobs Deleted";
			_log.info("Job|Deletion| {}", response);
			return response;
		} else {
			throw new Exception("Invalid Input");
		}

	}

	@GetMapping("/")
	public List<ScheduledJob> getJobs() throws Exception {
		Scheduler scheduler = schedulerFactoryBean.getScheduler();
		List<ScheduledJob> scheduledJobs = new ArrayList<ScheduledJob>();
		for (String groupName : scheduler.getJobGroupNames()) {
			for (JobKey jobKey : scheduler.getJobKeys(GroupMatcher.jobGroupEquals(groupName))) {
				List<Trigger> triggers = (List<Trigger>) scheduler.getTriggersOfJob(jobKey);
				ScheduledJob scheduledJob = new ScheduledJob();
				scheduledJob.setJobName(jobKey.getName());
				scheduledJob.setJobGroup(jobKey.getGroup());
				scheduledJob.setScheduledTime(triggers.get(0).getNextFireTime());
				scheduledJobs.add(scheduledJob);
			}
		}
		return scheduledJobs;
	}

}
