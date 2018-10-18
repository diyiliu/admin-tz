package com.tiza.web.deploy.dto;

import com.tiza.web.devops.dto.DevNode;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * Description: Deploy
 * Author: DIYILIU
 * Update: 2018-09-10 09:50
 */

@Data
@Entity
@Table(name = "dev_deploy")
public class Deploy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "node_id", referencedColumnName = "id")
    private DevNode node;

    /** 程序类型(0: 常规任务; 1: 实时任务; 2: 离线任务;) **/
    private Integer type;

    private String name;

    private String dir;

    private String jarFile;

    private String args;

    private Integer status;

    private Date uptime;

    private Date updateTime;

    @Transient
    private String uptimeStr;

    private Date createTime;
}
