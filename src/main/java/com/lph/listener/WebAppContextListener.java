package com.lph.listener;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.springframework.web.context.support.WebApplicationContextUtils;

import com.lph.util.Const;

/**
 * 类名称：WebAppContextListener.java
 *
 * @author lvpenghui
 * @since 2019-4-11 21:05:35
 */
public class WebAppContextListener implements ServletContextListener {

    @Override
    public void contextDestroyed(ServletContextEvent event) {
    }

    @Override
    public void contextInitialized(ServletContextEvent event) {
        Const.WEB_APP_CONTEXT = WebApplicationContextUtils.getWebApplicationContext(event.getServletContext());
        System.out.println("========获取Spring WebApplicationContext");
    }

}
