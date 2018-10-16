package com.tiza.web.sys.facade;

import com.tiza.web.sys.dto.SysAsset;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

/**
 * Description: SysAssetJpa
 * Author: DIYILIU
 * Update: 2018-04-28 14:23
 */

public interface SysAssetJpa extends JpaRepository<SysAsset, Long> {

    // List<SysAsset> findByIsMenuOrderByPidAscSortAsc(int isMenu);

    List<SysAsset> findByIsMenuAndIdInOrderByPidAscSortAsc(int isMenu, Set ids);

    List<SysAsset> findByIdIn(Set ids);

    SysAsset findByCode(String code);

    SysAsset findByController(String controller);

    @Transactional
    void deleteByIdIn(List<Long> ids);
}
