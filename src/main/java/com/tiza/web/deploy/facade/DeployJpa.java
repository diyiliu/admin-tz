package com.tiza.web.deploy.facade;

import com.tiza.web.deploy.dto.Deploy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Description: DeployJpa
 * Author: DIYILIU
 * Update: 2018-09-10 10:02
 */
public interface DeployJpa extends JpaRepository<Deploy, Long> {

    List<Deploy> findByNodeId(Long id);

    Page<Deploy> findByType(Integer type, Pageable pageable);
}
