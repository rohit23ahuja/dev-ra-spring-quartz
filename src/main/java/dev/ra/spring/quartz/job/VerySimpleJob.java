package dev.ra.spring.quartz.job;

import org.quartz.DateBuilder;
import org.quartz.DateBuilder.IntervalUnit;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobKey;
import org.quartz.PersistJobDataAfterExecution;
import org.quartz.Scheduler;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;

import dev.ra.spring.quartz.dto.DummyRequestDto;
import dev.ra.spring.quartz.dto.DummyResponseDto;
import dev.ra.spring.quartz.service.DummyService;
import lombok.Setter;

@PersistJobDataAfterExecution
@DisallowConcurrentExecution
@Setter
public class VerySimpleJob extends QuartzJobBean {

	private static Logger _log = LoggerFactory.getLogger(VerySimpleJob.class);
	public static final String COLOR = "color";
	public static final String EXECUTION_COUNT = "count";
	public static final String ERROR = "error";

	@Autowired
	private DummyService verySimpleJobService;
	

	private String count;
	private String color;


	@Override
	public void executeInternal(JobExecutionContext context) throws JobExecutionException {

		JobKey jobKey = context.getJobDetail().getKey();
		_log.info("VerySimpleJob|Execution Key {} ",jobKey);
		try {
			DummyRequestDto dummyRequestDto = new DummyRequestDto();
			dummyRequestDto.setFavoriteColor(color);
			DummyResponseDto dummyResponseDto = verySimpleJobService.doRelease(dummyRequestDto);
			if (dummyResponseDto.getResponse().equals("reschedule")) {
				Integer counter = Integer.parseInt(count);
				if (counter>=3) {
					_log.info("VerySimpleJob|Already tried multiple times.");					
				} else {
					JobDataMap data = context.getMergedJobDataMap();
					Scheduler scheduler = context.getScheduler();
			        VerySimpleJobCreator verySimpleJobCreator = VerySimpleJobCreator.getInstance(color);
			        TriggerKey triggerKey = verySimpleJobCreator.createTriggerKey();
					++counter;
					_log.info("VerySimpleJob|Rescheduling Job. Count {} ", counter);
					data.put(EXECUTION_COUNT, counter.toString());
					Trigger existingTrigger = scheduler
							.getTrigger(triggerKey);
					TriggerBuilder<? extends Trigger> triggerBuilder = existingTrigger.getTriggerBuilder();
					Trigger newTrigger = triggerBuilder.startAt(DateBuilder.futureDate(15, IntervalUnit.SECOND)).build();
					scheduler.rescheduleJob(triggerKey, newTrigger);
				}
				
			}

		} catch (Exception e) {
			_log.info("VerySimpleJob|Error", e);
			JobExecutionException e2 = new JobExecutionException(e);
			throw e2;
		}
	}

}
