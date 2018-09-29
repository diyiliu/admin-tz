package com.tiza.support.shiro.matcher;

import com.tiza.web.sys.dto.SysAsset;
import com.tiza.web.sys.dto.SysPrivilege;
import com.tiza.web.sys.dto.SysRole;
import com.tiza.web.sys.dto.SysUser;
import com.tiza.web.sys.facade.SysAssetJpa;
import com.tiza.web.sys.facade.SysPrivilegeJpa;
import com.tiza.web.sys.facade.SysUserJpa;
import org.apache.commons.collections.CollectionUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.ExcessiveAttemptsException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheManager;
import org.apache.shiro.session.Session;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * Description: RetryCredentialsMatcher
 * Author: DIYILIU
 * Update: 2018-05-03 13:31
 */

public class RetryCredentialsMatcher extends HashedCredentialsMatcher {

    private CacheManager cacheManager;

    private SysUserJpa sysUserJpa;

    private SysPrivilegeJpa sysPrivilegeJpa;

    private SysAssetJpa sysAssetJpa;

    public RetryCredentialsMatcher(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    @Override
    public boolean doCredentialsMatch(AuthenticationToken token, AuthenticationInfo info) {
        Cache<String, AtomicInteger> passwordRetryCache = cacheManager.getCache("passwordRetryCache");

        String username = (String) token.getPrincipal();
        //retry count + 1
        AtomicInteger retryCount = passwordRetryCache.get(username);
        if (retryCount == null) {
            retryCount = new AtomicInteger(0);
            passwordRetryCache.put(username, retryCount);
        }
        if (retryCount.incrementAndGet() > 5) {
            //if retry count > 5 throw
            throw new ExcessiveAttemptsException();
        }

        boolean matches = super.doCredentialsMatch(token, info);
        if (matches) {
            // 清除重试次数
            passwordRetryCache.remove(username);

            // 把用户信息放在session里
            Session session = SecurityUtils.getSubject().getSession();
            SysUser user = sysUserJpa.findByUsername(username);
            session.setAttribute("user", user);

            // 装载数据
            loadData(user, session);
            // 记录登录信息
            UsernamePasswordToken upToken = (UsernamePasswordToken) token;
            recordLogin(user, upToken.getHost());
        }

        return matches;
    }

    /**
     * 初始化菜单
     *
     * @param user
     */
    private void loadData(SysUser user,  Session session){
        List<SysRole> roleList = user.getRoles();
        if (CollectionUtils.isEmpty(roleList)){
            // 未分配角色
            return;
        }

        Set roles = roleList.stream().map(SysRole::getId).collect(Collectors.toSet());
        List<SysPrivilege> privilegeList = sysPrivilegeJpa.findByMasterAndMasterValueIn("role", roles);
        if (CollectionUtils.isEmpty(privilegeList)){
            // 无菜单权限
            return;
        }

        Set assets = privilegeList.stream().map(SysPrivilege::getAccessValue).collect(Collectors.toSet());
        // 权限节点(权限表中直接关联的节点)
        List<SysAsset> parts = sysAssetJpa.findByIdIn(assets);
        Set<Long> fulls = new HashSet();
        fulls.addAll(assets);
        for (SysAsset asset: parts){
            String[] pids = asset.getPids().split("/");
            for (String id: pids){
                fulls.add(Long.parseLong(id));
            }
        }
        // 完整节点(根据子节点查询所有上级节点)
        List<SysAsset> assetList = sysAssetJpa.findByIsMenuAndIdInOrderByPidAscSortAsc(1, fulls);
        // 根节点
        List<SysAsset> rootList = assetList.stream().filter(a -> a.getPid() == 0).collect(Collectors.toList());
        // 子节点
        Map<Long, List<SysAsset>> menuMap = assetList.stream().filter(a -> a.getPid() > 0).collect(Collectors.groupingBy(SysAsset::getPid));
        for (SysAsset asset: rootList){
            long id = asset.getId();
            if ("node".equals(asset.getType()) || menuMap.containsKey(id)){
                List<SysAsset> children = menuMap.get(id);
                if (CollectionUtils.isNotEmpty(children)){
                    asset.setChildren(children);
                }
            }
        }
        // 初始化菜单
        session.setAttribute("menus", rootList);

        // 设置当前页
        SysAsset asset = sysAssetJpa.findByCode("index");
        session.setAttribute("active", asset);
    }

    /**
     * 记录用户登录
     *
     * @param user
     * @param host
     */
    private void recordLogin(SysUser user, String host){
        user.setLoginCount(user.getLoginCount() == null? 1: user.getLoginCount() + 1);
        user.setLastLoginIp(host);
        user.setLastLoginTime(new Date());

        sysUserJpa.save(user);
    }


    public void setSysUserJpa(SysUserJpa sysUserJpa) {
        this.sysUserJpa = sysUserJpa;
    }

    public void setSysPrivilegeJpa(SysPrivilegeJpa sysPrivilegeJpa) {
        this.sysPrivilegeJpa = sysPrivilegeJpa;
    }

    public void setSysAssetJpa(SysAssetJpa sysAssetJpa) {
        this.sysAssetJpa = sysAssetJpa;
    }
}
