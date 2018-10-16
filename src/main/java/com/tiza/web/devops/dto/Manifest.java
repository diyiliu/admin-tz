package com.tiza.web.devops.dto;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * Description: Manifest
 * Author: DIYILIU
 * Update: 2018-09-12 14:47
 */

@Data
@Entity
@Table(name = "dev_manifest")
public class Manifest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String os;

    private String host;

    private String netNo;

    private String user;

    private String pwd;

    private String tag;

    private String note;

    private Date createTime;
}
