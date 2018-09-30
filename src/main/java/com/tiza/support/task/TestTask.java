package com.tiza.support.task;

import lombok.extern.slf4j.Slf4j;

/**
 * Description: TestTask
 * Author: DIYILIU
 * Update: 2018-09-30 10:02
 */

@Slf4j
public class TestTask extends TaskAdapter {

    @Override
    public void execute() {

        log.info("TestTask execute ... ");
    }
}
