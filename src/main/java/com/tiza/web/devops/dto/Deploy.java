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
@Table
public class Deploy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String host;

    private Integer port;

    private String user;

    private String pwd;

    private String path;

    private Date createTime;

    @Transient
    private Integer status;
}
