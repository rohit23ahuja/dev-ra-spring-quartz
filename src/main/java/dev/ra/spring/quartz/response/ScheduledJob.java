package dev.ra.spring.quartz.response;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ScheduledJob {

	private String jobName;
	private String jobGroup;
	private Date scheduledTime;
}
