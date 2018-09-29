package com.tiza.support.shiro.listener;

import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.SessionListener;
import org.springframework.stereotype.Component;

/**
 * Description: ShiroSessionListener
 * Author: DIYILIU
 * Update: 2018-09-20 09:42
 */

@Slf4j
@Component
public class ShiroSessionListener implements SessionListener {

    @Override
    public void onStart(Session session) {

        //log.info("会话创建 ... ");
    }

    @Override
    public void onStop(Session session) {
        //log.info("会话退出 ... ");

    }

    @Override
    public void onExpiration(Session session) {
        //log.info("会话过期 ... ");

    }
}
