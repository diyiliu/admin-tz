package com.tiza.support.shiro.filter;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.authc.FormAuthenticationFilter;
import org.apache.shiro.web.util.WebUtils;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

/**
 * Description: FormLoginFilter
 * Author: DIYILIU
 * Update: 2018-05-03 13:47
 */
public class FormLoginFilter extends FormAuthenticationFilter {

    @Override
    protected boolean executeLogin(ServletRequest request, ServletResponse response) throws Exception {
        AuthenticationToken token = createToken(request, response);
        if (token == null) {
            String msg = "createToken method implementation returned null. A valid non-null AuthenticationToken " +
                    "must be created in order to execute a login attempt.";
            throw new IllegalStateException(msg);
        }
        try {
            Subject subject = SecurityUtils.getSubject();
            subject.login(token);

            return onLoginSuccess(token, subject, request, response);
        } catch (AuthenticationException e) {
            return onLoginFailure(token, e, request, response);
        }
    }

    @Override
    protected AuthenticationToken createToken(ServletRequest request, ServletResponse response) {
        boolean rememberMe = isRememberMe(request);
        String host = getHost(request);

        return new UsernamePasswordToken(getUsername(request), getPassword(request), rememberMe, host);
    }

    @Override
    protected boolean onLoginSuccess(AuthenticationToken token, Subject subject, ServletRequest request, ServletResponse response) throws Exception {
        // 清理原始地址
        WebUtils.getAndClearSavedRequest(request);
        WebUtils.redirectToSavedRequest(request, response, getSuccessUrl());

        return false;
    }

    @Override
    public boolean isRememberMe(ServletRequest request) {
        return WebUtils.isTrue(request, getRememberMeParam());
    }
}
