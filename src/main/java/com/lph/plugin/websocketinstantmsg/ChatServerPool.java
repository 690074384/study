package com.lph.plugin.websocketinstantmsg;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.java_websocket.WebSocket;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 即时通讯
 *
 * @author lvpenghui
 * @since 2019-4-17 10:04:18
 */
class ChatServerPool {

    private static final Map<WebSocket, String> USER_CONNECTIONS = Maps.newHashMap();

    /**
     * 获取用户名
     *
     * @param conn WebSocket连接
     */
    static String getUserByKey(WebSocket conn) {
        return USER_CONNECTIONS.get(conn);
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
        List<String> setUsers = Lists.newArrayList();
        Collection<String> setUser = USER_CONNECTIONS.values();
        for (String u : setUser) {
            setUsers.add("<a onclick=\"toUserMsg('" + u + "');\">" + u + "</a>");
        }
        return setUsers;
    }

    /**
     * 移除连接池中的连接
     *
     * @param conn WebSocket连接
     */
    static boolean removeUser(WebSocket conn) {
        if (USER_CONNECTIONS.containsKey(conn)) {
            //移除连接
            USER_CONNECTIONS.remove(conn);
            return true;
        } else {
            return false;
        }
    }

    /**
     * 向特定的用户发送数据
     *
     * @param conn    WebSocket连接
     * @param message 消息
     */
    static void sendMessageToUser(WebSocket conn, String message) {
        if (null != conn && null != USER_CONNECTIONS.get(conn)) {
            conn.send(message);
        }
    }

    /**
     * 向所有的用户发送消息
     *
     * @param message 消息
     */
    static void sendMessage(String message) {
        Set<WebSocket> keySet = USER_CONNECTIONS.keySet();
        synchronized (keySet) {
            for (WebSocket conn : keySet) {
                String user = USER_CONNECTIONS.get(conn);
                if (user != null) {
                    conn.send(message);
                }
            }
        }
    }
}
