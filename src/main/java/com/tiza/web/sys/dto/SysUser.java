package com.tiza.web.sys.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Description: SysUser
 * Author: DIYILIU
 * Update: 2018-05-02 22:11
 */

@Data
@Entity
@Table(name = "sys_user")
public class SysUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;

    @JsonIgnore
    private String password;

    @JsonIgnore
    private String salt;

    private String name;

    @NotEmpty
    private String tel;

    @Email
    @NotEmpty
    private String email;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "rel_user_role",
    joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
    inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id"))
    private List<SysRole> roles;

    private Long orgId;

    private Date createTime;

    private String createUser;

    private Integer status;

    private String userIcon;

    private String note;

    private Date expireTime;

    private Integer loginCount;

    private String lastLoginIp;

    private Date lastLoginTime;

    @Transient
    @JsonIgnore
    private String expireTimeStr;

    public String getCredentialsSalt() {
        return username + salt;
    }

    public void setRoleIds(Long[] ids){
        roles = new ArrayList();
        for (Long id: ids){
            SysRole role = new SysRole();
            role.setId(id);

            roles.add(role);
        }
    }
}
