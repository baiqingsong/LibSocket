package com.dawn.socket;

import android.text.TextUtils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;

@SuppressWarnings("unused")
public class LSocketUtil {
    private static final String TAG = LSocketUtil.class.getSimpleName();
    private String ip;
    private int port;
    private SocketListener mListener;
    private Socket mSocket;
    private PrintWriter printWriter;
    private BufferedReader in;
    private boolean isClose = false;

    /**
     * socket连接
     * @param ip ip
     * @param port 端口
     * @param listener 回调
     */
    @SuppressWarnings("WeakerAccess")
    public void connect(final String ip, final int port, final SocketListener listener){
        this.ip = ip;
        this.port = port;
        this.mListener = listener;
        new Thread(){
            @Override
            public void run() {
                super.run();
                try{
                    isClose = false;
                    mSocket = new Socket();
                    InetSocketAddress isa = new InetSocketAddress(ip, port);
                    mSocket.connect(isa, 5000);
//                    mSocket.setSoTimeout(60000);
                    if(mSocket.isConnected()){
                        //连接成功
                        if(listener != null)
                            listener.connectSuccess();
                    }
                    printWriter = new PrintWriter(new BufferedWriter(new OutputStreamWriter(mSocket.getOutputStream(), "UTF-8")), true);
                    in = new BufferedReader(new InputStreamReader(mSocket.getInputStream(), "UTF-8"));
                    receiverMsg(listener);
                }catch (IOException e){
                    e.printStackTrace();
                    if(listener != null)
                        listener.connectFail();
                }
            }
        }.start();
    }

    /**
     * socket发送信息
     * @param msg 发送的字符串
     */
    public void sendMsg(final String msg){
        try{
            if(printWriter != null && !TextUtils.isEmpty(msg)){
                printWriter.println(msg);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 接收socket数据
     * @param listener 回调
     */
    private void receiverMsg(final SocketListener listener){
        try{
            while (true){
                if(in == null || mSocket == null)
                    break;
                String receiverStr;
                if((receiverStr = in.readLine()) != null){
                    if(listener != null)
                        listener.receiverMsg(receiverStr);
                }else{
                    //此处是服务器意外中断
                    if(listener != null)
                        listener.connectFail();
                    break;
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            if(!isClose)
                connect(ip, port, mListener);
        }
    }

    /**
     * socket断开连接
     */
    public void disConnect(){
        try {
            isClose = true;
            if(mSocket != null){
                mSocket.shutdownInput();
                mSocket.shutdownOutput();
                mSocket.close();
                mSocket = null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                if(mSocket != null){
                    mSocket.close();
                    mSocket = null;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    /**
     * 判断socket是否断开
     */
    public boolean isSocketClose(){
        if(mSocket == null)
            return true;
        try{
            mSocket.sendUrgentData(0);
            return false;
        }catch (Exception e){
            e.printStackTrace();
            return true;
        }
    }

    /**
     * socket回调接口
     */
    public interface SocketListener{
        void connectSuccess();
        void receiverMsg(String msg);
        void connectFail();
    }
}
