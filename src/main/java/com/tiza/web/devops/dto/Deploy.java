package com.tiza.web.devops.dto;

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

    private String name;

    private String path;

    private String args;

    private Date createTime;

    @Transient
    private Integer status;
}
