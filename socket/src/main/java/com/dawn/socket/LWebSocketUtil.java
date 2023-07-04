package com.dawn.socket;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

@SuppressWarnings({"unused", "WeakerAccess"})
public class LWebSocketUtil {
    private final static String TAG = LWebSocketUtil.class.getSimpleName();
    private Socket mSocket;
    private static LWebSocketUtil lWebSocketUtil;
    public static LWebSocketUtil SingleInstance(String url){
        if(lWebSocketUtil == null)
            lWebSocketUtil = new LWebSocketUtil(url);
        return lWebSocketUtil;
    }
    private LWebSocketUtil(String url) {
        createSocket(url);
    }

    /**
     * 创建socket
     * http://106.75.13.44:9090
     */
    public void createSocket(String url){
        try {
            mSocket = IO.socket(url);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    /**
     * 建立socket连接
     */
    public void connect(){
        if(mSocket != null && !mSocket.connected())
            mSocket.connect();
    }

    /**
     * 断开socket连接
     */
    public void disconnect(){
        if(mSocket != null){
            mSocket.disconnect();
            mSocket = null;
        }
    }

    /**
     * 判断当前的连接状态
     */
    public boolean isConnect(){
        if(mSocket != null)
            return mSocket.connected();
        return false;
    }

    /**
     * 根据event发送obj消息
     * @param event 指令
     * @param obj 内容
     */
    public void emit(String event, Object obj){
        if(mSocket != null)
            mSocket.emit(event, obj);
    }

    /**
     * 根据event接收并进行处理listener回调
     * @param event 指令
     * @param listener 回调
     */
    public void receiver(String event, WebSocketListener listener){
        if(mSocket != null)
            mSocket.on(event, listener);
    }

    public void send(Object obj){
        if(mSocket != null)
            mSocket.send(obj);
    }

    public interface WebSocketListener extends Emitter.Listener{

    }

}
