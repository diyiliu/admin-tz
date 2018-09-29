package com.tiza.support.filter;

import com.tiza.web.sys.dto.SysRole;
import com.tiza.web.sys.dto.SysUser;
import com.tiza.web.sys.facade.SysRoleJpa;
import com.tiza.web.sys.facade.SysUserJpa;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Description: PageDataBindingFilter
 * Author: DIYILIU
 * Update: 2018-05-11 10:14
 */

@Aspect
@Component
public class PageDataBindingFilter {

    @Resource
    private SysRoleJpa sysRoleJpa;

    @Resource
    private SysUserJpa sysUserJpa;

    @After("execution(* com.tiza.web.HomeController.show(..))")
    public void doAfter(JoinPoint joinPoint) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();

        Object[] args = joinPoint.getArgs();
        String menu = (String) args[0];

        if ("user".equals(menu)) {
            List<SysRole> roleList = sysRoleJpa.findAll();
            request.setAttribute("roles", roleList);

            List<SysUser> userList = sysUserJpa.findAll();
            List<String> usernameList = userList.stream().map(SysUser::getUsername).collect(Collectors.toList());
            List<String> nameList = userList.stream().map(SysUser::getName).collect(Collectors.toList());
            nameList.addAll(usernameList);
            request.setAttribute("names", nameList);

            return;
        }
    }
}
