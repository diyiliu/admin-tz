package com.tiza.web.deploy.facade;

import com.tiza.web.deploy.dto.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Description: ScheduleJpa
 * Author: DIYILIU
 * Update: 2018-10-23 14:16
 */
public interface ScheduleJpa extends JpaRepository<Schedule, Long> {


}
