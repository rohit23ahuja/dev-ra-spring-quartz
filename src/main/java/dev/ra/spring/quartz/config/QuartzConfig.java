package dev.ra.spring.quartz.config;

import java.io.IOException;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

@Configuration
public class QuartzConfig {

	@Autowired
	private DataSource dataSource;

	@Autowired
	private ApplicationContext applicationContext;

	@Bean
	public SchedulerFactoryBean schedulerFactoryBean() throws IOException {

		SpringBeanJobFactoryWithAutoWiring jobFactory = new SpringBeanJobFactoryWithAutoWiring();
		jobFactory.setApplicationContext(applicationContext);

		SchedulerFactoryBean factory = new SchedulerFactoryBean();
		factory.setOverwriteExistingJobs(true);
		factory.setDataSource(dataSource);
		factory.setConfigLocation(new ClassPathResource("quartz.properties"));

		factory.setJobFactory(jobFactory);
		return factory;
	}

}
