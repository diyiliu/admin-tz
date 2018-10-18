package com.tiza.web.devops.facade;

import com.tiza.web.devops.dto.NodeStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

/**
 * Description: NodeStatusJpa
 * Author: DIYILIU
 * Update: 2018-10-16 09:03
 */
public interface NodeStatusJpa extends JpaRepository<NodeStatus, Long> {

    @Query("select t from NodeStatus t where t.node.checkOn = 1")
    Page<NodeStatus> findByNodeCheckOn(Pageable pageable);

    NodeStatus findByNodeId(Long id);

    @Transactional
    void deleteByNodeId(Long id);
}
