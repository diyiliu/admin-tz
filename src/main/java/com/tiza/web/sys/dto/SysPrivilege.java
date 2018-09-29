package com.tiza.web.sys.dto;

import lombok.Data;

import javax.persistence.*;

/**
 * Description: SysPrivilege
 * Author: DIYILIU
 * Update: 2018-05-02 22:44
 */

@Data
@Entity
@Table(name = "sys_privilege")
public class SysPrivilege {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String master;

    private Long masterValue;

    private String access;

    private Long accessValue;

    private String permission;

    private String comment;
}
