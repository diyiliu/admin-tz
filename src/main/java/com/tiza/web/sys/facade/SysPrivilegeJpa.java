package com.tiza.web.sys.facade;

import com.tiza.web.sys.dto.SysPrivilege;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * Description: SysPrivilegeJpa
 * Author: DIYILIU
 * Update: 2018-05-03 13:20
 */
public interface SysPrivilegeJpa extends JpaRepository<SysPrivilege, Long> {

    List<SysPrivilege> findByMasterAndMasterValue(String master, long masterValue);

    List<SysPrivilege> findByMasterAndMasterValueIn(String master, Set set);

    @Transactional
    void deleteByMasterAndMasterValue(String master, long masterValue);
/*
    @Query(value = "SELECT CONCAT(IFNULL(p.`code`, 'home'), ':', t.`code`), t.type" +
            " FROM sys_asset t LEFT JOIN sys_asset p ON p.id = t.pid WHERE t.id in ?1", nativeQuery = true)
    List findByAssetIdIn(List list);
*/
}
