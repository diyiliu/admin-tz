package com.tiza.web.devops.dto;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * Description: NodeStatus
 * Author: DIYILIU
 * Update: 2018-10-15 17:00
 */

@Data
@Entity
@Table(name = "dev_node_status")
public class NodeStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "node_id", referencedColumnName = "id")
    private DevNode node;

    private Integer cpuUsage;

    private Integer memoryUsage;

    private String processInfo;

    private String diskInfo;

    private Integer status;

    private Date updateTime;
}
