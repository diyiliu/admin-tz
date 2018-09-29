package com.tiza.web.devops.facade;

import com.tiza.web.devops.dto.Deploy;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Description: DeployJpa
 * Author: DIYILIU
 * Update: 2018-09-10 10:02
 */
public interface DeployJpa extends JpaRepository<Deploy, Long> {

}
