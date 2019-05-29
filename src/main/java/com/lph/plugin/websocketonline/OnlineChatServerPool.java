package com.lph.plugin.websocketonline;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.java_websocket.WebSocket;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * 在线管理
 *
 * @author lvpenghui
 * @since 2019-4-17 10:15:53
 */
public class OnlineChatServerPool {

    private static final Map<WebSocket, String> USER_CONNECTIONS = Maps.newHashMap();

    private static WebSocket fhadmin = null;
    ;

    /**
     * 获取用户名
     *
     * @param conn WebSocket连接
     */
    public static String getUserByKey(WebSocket conn) {
        return USER_CONNECTIONS.get(conn);
    }

    /**
     * 获取在线总数
     */
    static int getUserCount() {
        return USER_CONNECTIONS.size();
    }

    /**
     * 获取WebSocket
     *
     * @param user 用户
     */
    static WebSocket getWebSocketByUser(String user) {
        Set<WebSocket> keySet = USER_CONNECTIONS.keySet();
        synchronized (keySet) {
            for (WebSocket conn : keySet) {
                String cuser = USER_CONNECTIONS.get(conn);
                if (cuser.equals(user)) {
                    return conn;
                }
            }
        }
        return null;
    }

    /**
     * 向连接池中添加连接
     *
     * @param user 用户
     * @param conn WebSocket连接
     */
    static void addUser(String user, WebSocket conn) {
        //添加连接
        USER_CONNECTIONS.put(conn, user);
    }

    /**
     * 获取所有的在线用户
     *
     * @return 在线用户集合
     */
    static Collection<String> getOnlineUser() {
        return Lists.newArrayList(USER_CONNECTIONS.values());
    }

    /**
     * 移除连接池中的连接
     *
     * @param conn WebSocket连接
     */
    static void removeUser(WebSocket conn) {
        //移除连接
        USER_CONNECTIONS.remove(conn);

    }

    /**
     * 向特定的用户发送数据
     *
     * @param conn    WebSocket连接
     * @param message 消息
     */
    static void sendMessageToUser(WebSocket conn, String message) {
        if (null != conn) {
            conn.send(message);
        }
    }

    public static WebSocket getFhadmin() {
        return fhadmin;
    }

    public static void setFhadmin(WebSocket fhadmin) {
        OnlineChatServerPool.fhadmin = fhadmin;
    }
}
