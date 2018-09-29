package com.tiza.web.sys.dto;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * Description: SysRole
 * Author: DIYILIU
 * Update: 2018-05-02 22:34
 */

@Data
@Entity
@Table(name = "sys_role")
public class SysRole {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long pid;

    private String name;

    private String code;

    private String comment;

    private String createUser;

    private Date createTime;
}
