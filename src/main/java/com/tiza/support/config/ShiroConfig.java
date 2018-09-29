package com.tiza.support.config;

import com.tiza.support.config.properties.ShiroProperties;
import com.tiza.support.shiro.cache.SpringCacheManager;
import com.tiza.support.shiro.filter.FormLoginFilter;
import com.tiza.support.shiro.helper.PasswordHelper;
import com.tiza.support.shiro.listener.ShiroSessionListener;
import com.tiza.support.shiro.matcher.RetryCredentialsMatcher;
import com.tiza.support.shiro.realm.UserRealm;
import com.tiza.web.sys.facade.SysAssetJpa;
import com.tiza.web.sys.facade.SysPrivilegeJpa;
import com.tiza.web.sys.facade.SysUserJpa;
import org.apache.shiro.session.mgt.SessionManager;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;
import javax.servlet.Filter;
import java.util.*;

/**
 * Description: ShiroConfig
 * Author: DIYILIU
 * Update: 2018-05-02 21:52
 */

@EnableCaching
@Configuration
@EnableConfigurationProperties(ShiroProperties.class)
public class ShiroConfig {

    @Resource
    private SysUserJpa sysUserJpa;

    @Resource
    private SysPrivilegeJpa sysPrivilegeJpa;

    @Resource
    private SysAssetJpa sysAssetJpa;

    @Autowired
    private ShiroProperties shiroProperties;

    @Autowired
    private ShiroSessionListener sessionListener;


    /**
     * 密码重试
     *
     * @param cacheManager
     * @return
     */
    @Bean
    public RetryCredentialsMatcher retryCredentialsMatcher(SpringCacheManager cacheManager) {
        RetryCredentialsMatcher matcher = new RetryCredentialsMatcher(cacheManager);
        matcher.setHashAlgorithmName(shiroProperties.getHashAlgorithmName());
        matcher.setHashIterations(shiroProperties.getHashIterations());
        matcher.setStoredCredentialsHexEncoded(true);

        matcher.setSysUserJpa(sysUserJpa);
        matcher.setSysAssetJpa(sysAssetJpa);
        matcher.setSysPrivilegeJpa(sysPrivilegeJpa);

        return matcher;
    }

    /**
     * realm实现
     *
     * @return
     */
    @Bean
    public UserRealm userRealm(RetryCredentialsMatcher retryCredentialsMatcher) {
        UserRealm userRealm = new UserRealm();
        userRealm.setCachingEnabled(true);
        userRealm.setAuthenticationCachingEnabled(true);
        userRealm.setAuthenticationCacheName("authenticationCache");
        userRealm.setAuthorizationCachingEnabled(true);
        userRealm.setAuthorizationCacheName("authorizationCache");
        userRealm.setCredentialsMatcher(retryCredentialsMatcher);

        userRealm.setSysUserJpa(sysUserJpa);
        userRealm.setSysPrivilegeJpa(sysPrivilegeJpa);

        return userRealm;
    }

    /**
     * 缓存管理器
     *
     * @param ehCacheCacheManager
     * @return
     */
    @Bean
    public SpringCacheManager cacheManagerWrapper(EhCacheCacheManager ehCacheCacheManager) {
        SpringCacheManager cacheManager = new SpringCacheManager();
        cacheManager.setCacheManager(ehCacheCacheManager);

        return cacheManager;
    }

    /**
     * 会话管理器
     *
     * @return
     */
    @Bean
    public SessionManager sessionManager() {
        DefaultWebSessionManager sessionManager = new DefaultWebSessionManager();
        // session 过期时间
        sessionManager.setGlobalSessionTimeout(1800000);
        sessionManager.setSessionIdUrlRewritingEnabled(false);

        sessionManager.setSessionListeners(new ArrayList(){
            {
                this.add(sessionListener);
            }
        });

        return sessionManager;
    }

    /**
     * 安全管理器
     *
     * @return
     */
    @Bean
    public DefaultWebSecurityManager securityManager(UserRealm userRealm, SpringCacheManager cacheManager) {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        securityManager.setRealm(userRealm);
        securityManager.setCacheManager(cacheManager);
        securityManager.setSessionManager(sessionManager());

        return securityManager;
    }

    /**
     * shiro过滤器
     *
     * @return
     */
    @Bean
    public ShiroFilterFactoryBean shiroFilter(DefaultWebSecurityManager securityManager) {
        ShiroFilterFactoryBean factoryBean = new ShiroFilterFactoryBean();
        factoryBean.setSecurityManager(securityManager);
        factoryBean.setLoginUrl(shiroProperties.getLoginUrl());
        factoryBean.setSuccessUrl(shiroProperties.getSuccessUrl());

        Map<String, Filter> filters = new LinkedHashMap<>();
        filters.put("authc", formAuthenticationFilter());
        factoryBean.setFilters(filters);

        Map<String, String> filterChain = shiroProperties.getFilterChain();
        Map<String, String> filterChainDefinitionMap = new LinkedHashMap();
        filterChain.keySet().stream().forEach(e -> {
            String value = filterChain.get(e);
            for (String key: value.split(",")){

                filterChainDefinitionMap.put(key, e);
            }
        });

        factoryBean.setFilterChainDefinitionMap(filterChainDefinitionMap);

        return factoryBean;
    }

    /**
     * 表单身份验证过滤器
     *
     * @return
     */
    @Bean
    public FormLoginFilter formAuthenticationFilter() {
        FormLoginFilter formLoginFilter = new FormLoginFilter();
        formLoginFilter.setLoginUrl(shiroProperties.getLoginUrl());
        formLoginFilter.setSuccessUrl(shiroProperties.getSuccessUrl());
        formLoginFilter.setUsernameParam(shiroProperties.getUsernameParam());
        formLoginFilter.setPasswordParam(shiroProperties.getPasswordParam());
        formLoginFilter.setRememberMeParam(shiroProperties.getRememberMeParam());

        return formLoginFilter;
    }


    @Bean
    public PasswordHelper passwordHelper() {
        PasswordHelper passwordHelper = new PasswordHelper();
        passwordHelper.setHashAlgorithmName(shiroProperties.getHashAlgorithmName());
        passwordHelper.setHashIterations(shiroProperties.getHashIterations());

        return passwordHelper;
    }
}
