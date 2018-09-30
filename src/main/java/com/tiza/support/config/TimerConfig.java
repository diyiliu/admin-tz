package com.tiza.support.config;

import com.tiza.support.task.TestTask;
import org.quartz.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import javax.annotation.Resource;

/**
 * Description: TimerConfig
 * Author: DIYILIU
 * Update: 2018-09-30 09:38
 */

@Configuration
@EnableScheduling
public class TimerConfig {

    @Resource
    private SchedulerFactoryBean schedulerFactoryBean;

/*
    @Bean
    public TestTask testTask() throws Exception {
        Scheduler scheduler = schedulerFactoryBean.getScheduler();

        JobDetail jobDetail = JobBuilder.newJob(TestTask.class).withIdentity("test").build();
        CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule("0/5 * * * * ?");
        CronTrigger cronTrigger = TriggerBuilder.newTrigger().withSchedule(scheduleBuilder).build();
        scheduler.scheduleJob(jobDetail, cronTrigger);

        return null;
    }

    @Scheduled(initialDelay = 2 * 60 * 1000, fixedDelay = 30 * 1000)
    public void stopTask() throws Exception {
        Scheduler scheduler = schedulerFactoryBean.getScheduler();

        JobKey jobKey = new JobKey("test");
        if (scheduler.checkExists(jobKey)) {
            scheduler.deleteJob(jobKey);

            System.out.println("stop task ...");
        }else {
            System.out.println("checkExists false ...");
        }
    }
*/

}
