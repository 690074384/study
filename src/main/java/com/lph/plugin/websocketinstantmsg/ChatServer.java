package com.lph.plugin.websocketinstantmsg;

import net.sf.json.JSONObject;
import org.java_websocket.WebSocket;
import org.java_websocket.WebSocketImpl;
import org.java_websocket.framing.Framedata;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Date;


/**
 * 即时通讯
 *
 * @author lvpenghui
 * @since 2019-4-11 21:05:51
 */
public class ChatServer extends WebSocketServer {

    public ChatServer(int port) {
        super(new InetSocketAddress(port));
    }

    public ChatServer(InetSocketAddress address) {
        super(address);
    }

    /**
     * 触发连接事件
     */
    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        //this.sendToAll( "new connection: " + handshake.getResourceDescriptor() );
        //System.out.println("===" + conn.getRemoteSocketAddress().getAddress().getHostAddress());
    }

    /**
     * 触发关闭事件
     */
    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        userLeave(conn);
    }

    /**
     * 客户端发送消息到服务器时触发事件
     */
    @Override
    public void onMessage(WebSocket conn, String message) {
        if (null != message && message.startsWith("FHadminqq313596790")) {
            this.userjoin(message.replaceFirst("FHadminqq313596790", ""), conn);
        }
        if (null != message && message.startsWith("LeaveFHadminqq313596790")) {
            this.userLeave(conn);
        }
        if (null != message && message.contains("fhadmin886")) {
            String toUser = message.substring(message.indexOf("fhadmin886") + 10, message.indexOf("fhfhadmin888"));
            message = message.substring(0, message.indexOf("fhadmin886")) + "[私信]  " + message.substring(message.indexOf("fhfhadmin888") + 12);
            //向所某用户发送消息
            ChatServerPool.sendMessageToUser(ChatServerPool.getWebSocketByUser(toUser), message);
            //同时向本人发送消息
            ChatServerPool.sendMessageToUser(conn, message);
        } else {
            //向所有在线用户发送消息
            ChatServerPool.sendMessage(message);
        }
    }

    public void onFragment(WebSocket conn, Framedata fragment) {
    }

    /**
     * 触发异常事件
     */
    @Override
    public void onError(WebSocket conn, Exception ex) {
        ex.printStackTrace();
        if (conn != null) {
            //some errors like port binding failed may not be assignable to a specific websocket
        }
    }


    /**
     * 用户加入处理
     *
     * @param user
     */
    private void userjoin(String user, WebSocket conn) {
        JSONObject result = new JSONObject();
        result.element("type", "user_join");
        result.element("user", "<a onclick=\"toUserMsg('" + user + "');\">" + user + "</a>");
        //把当前用户加入到所有在线用户列表中
        ChatServerPool.sendMessage(result.toString());
        String joinMsg = "{\"from\":\"[系统]\",\"content\":\"" + user + "上线了\",\"timestamp\":" + System.currentTimeMillis() + ",\"type\":\"message\"}";
        //向所有在线用户推送当前用户上线的消息
        ChatServerPool.sendMessage(joinMsg);
        result = new JSONObject();
        result.element("type", "get_online_user");
        //向连接池添加当前的连接对象
        ChatServerPool.addUser(user, conn);
        result.element("list", ChatServerPool.getOnlineUser());
        //向当前连接发送当前在线用户的列表
        ChatServerPool.sendMessageToUser(conn, result.toString());
    }

    /**
     * 用户下线处理
     *
     * @param conn websocket长连接
     */
    private void userLeave(WebSocket conn) {
        String user = ChatServerPool.getUserByKey(conn);
        //在连接池中移除连接
        boolean b = ChatServerPool.removeUser(conn);
        if (b) {
            JSONObject result = new JSONObject();
            result.element("type", "user_leave");
            result.element("user", "<a onclick=\"toUserMsg('" + user + "');\">" + user + "</a>");
            //把当前用户从所有在线用户列表中删除
            ChatServerPool.sendMessage(result.toString());
            String joinMsg = "{\"from\":\"[系统]\",\"content\":\"" + user + "下线了\",\"timestamp\":" + System.currentTimeMillis() + ",\"type\":\"message\"}";
            //向在线用户发送当前用户退出的消息
            ChatServerPool.sendMessage(joinMsg);
        }
    }

    public static void main(String[] args) {
        WebSocketImpl.DEBUG = false;
        //端口
        int port = 8887;
        ChatServer s = new ChatServer(port);
        s.start();
        //System.out.println( "服务器的端口" + s.getPort() );
    }

}

