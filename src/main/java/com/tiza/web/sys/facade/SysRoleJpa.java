package com.tiza.web.sys.facade;

import com.tiza.web.sys.dto.SysRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Description: SysRoleJpa
 * Author: DIYILIU
 * Update: 2018-05-02 22:35
 */
public interface SysRoleJpa extends JpaRepository<SysRole, Long> {

/*
    @Query(value = "SELECT r.id,r.name ,r.code ,r.comment FROM sys_role r " +
            "INNER JOIN sys_user u ON u.username = ? " +
            "INNER JOIN rel_user_role t ON t.user_id = u.id AND t.role_id = r.id", nativeQuery = true)
    List<SysRole> findByUser(String username);
*/

    @Transactional
    void deleteByIdIn(List<Long> ids);
}
