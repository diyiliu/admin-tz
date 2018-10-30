package com.tiza.web.deploy.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * 定时任务
 * Description: Schedule
 * Author: DIYILIU
 * Update: 2018-10-23 14:09
 */
@Data
@Entity
@Table(name = "dev_cron")
public class Schedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @OneToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "deploy_id", referencedColumnName = "id")
    private Deploy deploy;

    private String cron;

    /** 0:停用; 1:启用; **/
    private Integer status;

    private Date executeTime;

    private Date createTime;
}
