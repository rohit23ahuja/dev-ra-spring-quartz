package dev.ra.spring.quartz.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ScheduleJobRequest {

	private String runTime;
	private String color;
}
