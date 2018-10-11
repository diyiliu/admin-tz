package com.tiza.web.devops.dto;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * Description: DevNode
 * Author: DIYILIU
 * Update: 2018-10-11 10:17
 */

@Data
@Entity
@Table(name = "dev_node")
public class DevNode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String type;

    private String os;

    private Integer cpuCore;

    private Integer memorySize;

    private Integer diskSize;

    private String host;

    private Integer port;

    private String user;

    private String pwd;

    private Integer status;

    private Integer checkOn;

    private Date createTime;
}
