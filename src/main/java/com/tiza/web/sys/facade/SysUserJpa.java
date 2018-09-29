package com.tiza.web.sys.facade;

import com.tiza.web.sys.dto.SysUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Description: SysUserJpa
 * Author: DIYILIU
 * Update: 2018-05-02 22:15
 */
public interface SysUserJpa extends JpaRepository<SysUser, Long> {

    Page<SysUser> findByUsernameLikeOrNameLike(String like1, String like2, Pageable pageable);

    SysUser findByUsername(String username);

    @Transactional
    void deleteByIdIn(List<Long> ids);
}
