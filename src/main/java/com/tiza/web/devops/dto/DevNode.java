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

    public DevNode() {
    }

    public DevNode(long id) {
        this.id = id;
    }

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

    /** 监控程序路径 **/
    private String path;

    private Integer checkOn;

    private Date createTime;
}
