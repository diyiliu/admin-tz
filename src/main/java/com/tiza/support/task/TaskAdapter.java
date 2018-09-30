package com.tiza.support.task;

import org.quartz.Job;
import org.quartz.JobExecutionContext;

/**
 * Description: TaskAdapter
 * Author: DIYILIU
 * Update: 2018-09-30 09:56
 */
public abstract class TaskAdapter implements Job, Runnable {

    public abstract void execute();

    @Override
    public void execute(JobExecutionContext context) {

        execute();
    }

    @Override
    public void run() {

        execute();
    }
}
