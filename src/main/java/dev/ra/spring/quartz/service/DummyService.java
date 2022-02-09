package dev.ra.spring.quartz.service;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import dev.ra.spring.quartz.dto.DummyRequestDto;
import dev.ra.spring.quartz.dto.DummyResponseDto;
import dev.ra.spring.quartz.job.VerySimpleJob;

@Service
public class DummyService {

	private static Logger _log = LoggerFactory.getLogger(VerySimpleJob.class);

	public DummyResponseDto doRelease(DummyRequestDto dummyRequestDto) {
		String favoriteColor = dummyRequestDto.getFavoriteColor();
		String response = "success";
		DummyResponseDto dummyResponseDto = new DummyResponseDto();
		if (!favoriteColor.equals("Black")) {
			_log.info("Date " + new Date() + " favorite color is " + favoriteColor);
			dummyResponseDto.setResponse(response);
			return dummyResponseDto;
		} else {
			dummyResponseDto.setResponse("reschedule");
			return dummyResponseDto;
		}
	}
}
