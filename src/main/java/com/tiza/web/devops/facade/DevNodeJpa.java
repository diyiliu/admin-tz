package com.tiza.web.devops.facade;

import com.tiza.web.devops.dto.DevNode;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Description: DevNodeJpa
 * Author: DIYILIU
 * Update: 2018-10-11 10:21
 */
public interface DevNodeJpa extends JpaRepository<DevNode, Long> {

    DevNode findByHost(String ip);
}
