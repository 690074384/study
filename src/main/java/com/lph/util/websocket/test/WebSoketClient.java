package com.lph.util.websocket.test;

import com.lph.util.websocket.WebSocketBase;
import com.lph.util.websocket.WebSocketService;

/**
 * 通过继承WebSocketBase创建WebSocket客户端
 *
 * @author okcoin
 */
class WebSoketClient extends WebSocketBase {
    WebSoketClient(String url, WebSocketService service) {
        super(url, service);
    }
}
