package com.tiza.web.devops.facade;

import com.tiza.web.devops.dto.Deploy;
import com.tiza.web.devops.dto.DevNode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Description: DeployJpa
 * Author: DIYILIU
 * Update: 2018-09-10 10:02
 */
public interface DeployJpa extends JpaRepository<Deploy, Long> {

    List<Deploy> findByNode(DevNode node);
}
