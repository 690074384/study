package com.lph.filter;

import java.io.IOException;
import java.net.UnknownHostException;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ScheduledExecutorService;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import com.lph.util.Constants;
import org.apache.commons.lang.StringUtils;
import org.java_websocket.WebSocketImpl;

import com.lph.plugin.websocketInstantMsg.ChatServer;
import com.lph.plugin.websocketOnline.OnlineChatServer;
import com.lph.util.Const;
import com.lph.util.DbFH;
import com.lph.util.Tools;
import com.lph.controller.base.BaseController;

/**
 * 启动tomcat时运行此类
 *
 * @author lvpenghui
 * @since 2019-4-11 20:58:00
 */
public class StartFilter extends BaseController implements Filter {

    /**
     * 初始化
     */
    @Override
    public void init(FilterConfig fc) {
        this.startWebsocketInstantMsg();
        this.startWebsocketOnline();
        this.reductionDbBackupQuartzState();
    }

    /**
     * 启动即时聊天服务
     */
    private void startWebsocketInstantMsg() {
        WebSocketImpl.DEBUG = false;
        ChatServer s;
        try {
            //读取WEBSOCKET配置,获取端口配置
            String websocketStr = Tools.readTxtFile(Const.WEBSOCKET);
            if (StringUtils.isNotEmpty(websocketStr)) {
                String[] strIw = websocketStr.split(",fh,");
                if (strIw.length == Constants.FIVE) {
                    s = new ChatServer(Integer.parseInt(strIw[1]));
                    s.start();
                }
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    /**
     * 启动在线管理服务
     */
    private void startWebsocketOnline() {
        WebSocketImpl.DEBUG = false;
        OnlineChatServer s;
        try {
            //读取WEBSOCKET配置,获取端口配置
            String strWEBSOCKET = Tools.readTxtFile(Const.WEBSOCKET);
            if (null != strWEBSOCKET && !"".equals(strWEBSOCKET)) {
                String[] strIw = strWEBSOCKET.split(",fh,");
                if (strIw.length == Constants.FIVE) {
                    s = new OnlineChatServer(Integer.parseInt(strIw[3]));
                    s.start();
                }
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    /**
     * web容器重启时，所有定时备份状态关闭
     */
    private void reductionDbBackupQuartzState() {
        try {
            DbFH.executeUpdateFH("update DB_TIMINGBACKUP set STATUS = '2'");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 计时器(废弃)用quartz代替
     */
    public void timer() {
        Calendar calendar = Calendar.getInstance();
        // 控制时
        calendar.set(Calendar.HOUR_OF_DAY, 9);
        // 控制分
        calendar.set(Calendar.MINUTE, 0);
        // 控制秒
        calendar.set(Calendar.SECOND, 0);
        // 得出执行任务的时间
        Date time = calendar.getTime();
        Timer timer = new Timer();
        // 这里设定将延时每天固定执行
        timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                //PersonService personService = (PersonService)ApplicationContext.getBean("personService");
                //System.out.println("-------设定要指定任务--------");
            }
        }, time, 1000 * 60 * 60 * 24);
    }

    @Override
    public void destroy() {
    }

    @Override
    public void doFilter(ServletRequest arg0, ServletResponse arg1,
                         FilterChain arg2) {
    }

}
