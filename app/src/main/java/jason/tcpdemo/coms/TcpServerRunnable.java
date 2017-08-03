package jason.tcpdemo.coms;

import android.content.Intent;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Date;

import jason.tcpdemo.funcs.ActivityFuncTcpServer;

/**
 * Created by Jason Zhu on 2017-04-24.
 * Email: cloud_happy@163.com
 */

public class TcpServerRunnable implements Runnable {
    private String TAG = "TcpServerRunnable";
    private int port = 1234;
    private boolean isListen = true;   //线程监听标志位
    public ArrayList<ServerSocketThread> SST = new ArrayList<ServerSocketThread>();
    private ServerSocket serverSocket;

    public TcpServerRunnable(int port) {
        this.port = port;
    }

    //更改监听标志位
    public void setIsListen(boolean b) {
        isListen = b;
    }

    public void closeSelf() {
        isListen = false;
        for (ServerSocketThread s : SST) {
            s.isRun = false;
        }
        SST.clear();
        if (serverSocket != null) {
            try {
                serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
                Log.e(TAG, "TcpServerRunnable closeSelf  IOException "
                        + this.toString()
                        + "  serverSocket=" + serverSocket.toString()
                        + "  "+e.toString());

            }
        }
    }

    private Socket getSocket(ServerSocket serverSocket) {
        try {
            return serverSocket.accept();
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "TcpServerRunnable run getSocket IOException  "  + this.toString()
                    + "  serverSocket=" + serverSocket.toString()
                    + "  "+e.toString());
            return null;
        }
    }

    @Override
    public void run() {
        try {
            serverSocket = new ServerSocket(port);
            serverSocket.setSoTimeout(5000);
            while (isListen) {
                Log.i(TAG, "run: 开始监听..."  + this.toString()
                        + "  serverSocket=" + serverSocket.toString()
                       );
                Socket socket = getSocket(serverSocket);
                if (socket != null) {
                    new ServerSocketThread(socket);
                }
            }
            serverSocket.close();
        } catch (SocketException e) {
            e.printStackTrace();
            Log.e(TAG, "TcpServerRunnable run  SocketTimeoutException  "  + this.toString()
                    + "  serverSocket=" + serverSocket.toString()
                    + "  "+e.toString());

        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "TcpServerRunnable run  IOException  "  + this.toString()
                    + "  serverSocket=" + serverSocket.toString()
                    + "  "+e.toString());

        }
    }

    public class ServerSocketThread extends Thread {
        public Socket socket = null;
        private PrintWriter pw;
        private InputStream is = null;
        private OutputStream os = null;
        private String ip = null;
        private boolean isRun = true;

        ServerSocketThread(Socket socket) {
            this.socket = socket;
            ip = socket.getInetAddress().toString();
            Log.i(TAG, "ServerSocketThread:检测到新的客户端联入,ip:" + ip + "  "
                    + "  socket=" + socket.toString()
                    + this.toString()
            );
            Intent intent = new Intent();
            intent.setAction("tcpServerReceiver");
            intent.putExtra("tcpServerReceiver", "ServerSocketThread:检测到新的客户端联入,ip:" + ip);
            ActivityFuncTcpServer.context.sendBroadcast(intent);//将消息发送给主界面
            try {
                socket.setSoTimeout(5000);
                os = socket.getOutputStream();
                is = socket.getInputStream();
                pw = new PrintWriter(os, true);
                send("" + new Date().toString() + "服务器已收到连接请求并建立连接");
                start();
            } catch (SocketException e) {
                e.printStackTrace();
                Log.e(TAG, "ServerSocketThread  SocketException "
                        + this.toString()
                        + "  socket=" + socket.toString()
                        + "  " + e.toString()
                );

            } catch (IOException e) {
                e.printStackTrace();
                Log.e(TAG, "ServerSocketThread  IOException "
                        + this.toString()
                        + "  socket=" + socket.toString()
                        + "  " + e.toString());

            }
        }

        public void send(String msg) {
            pw.println(msg);
            pw.flush(); //强制送出数据
        }

        @Override
        public void run() {
            byte buff[] = new byte[4096];
            String rcvMsg;
            int rcvLen;
            SST.add(this);
            while (isRun && !socket.isClosed() && !socket.isInputShutdown()) {
                try {
                    if ((rcvLen = is.read(buff)) != -1) {
                        rcvMsg = new String(buff, 0, rcvLen, "utf-8");
                        Log.i(TAG, "run:收到消息: " + rcvMsg);
                        Intent intent = new Intent();
                        intent.setAction("tcpServerReceiver");
                        intent.putExtra("tcpServerReceiver", rcvMsg);
                        ActivityFuncTcpServer.context.sendBroadcast(intent);//将消息发送给主界面
                        if (rcvMsg.equals("QuitServer")) {
                            isRun = false;
                        }
                    }
                } catch (SocketException e) {
                    e.printStackTrace();
                    Log.e(TAG, "ServerSocketThread run SocketException  "
                            + this.toString()
                            + "  socket=" + socket.toString()
                            + "  " + e.toString());
                    isRun = false;//客户端主动断开 会抛异常
                } catch (SocketTimeoutException e) {
                    e.printStackTrace();
                    Log.e(TAG, "ServerSocketThread run SocketTimeoutException  "
                            + this.toString()
                            + "  socket=" + socket.toString()
                            + "  " + e.toString());
                    if (!socket.isConnected())
                        isRun = false;//超时停止循环断开该socket； 服务器与客户端 超时无数据传输 抛异常；超时仅仅抛异常并不会导致socket主动断开
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                    Log.e(TAG, "ServerSocketThread run UnsupportedEncodingException "
                            + this.toString()
                            + "  socket=" + socket.toString()
                            + "  " + e.toString());
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e(TAG, "ServerSocketThread run IOException "
                            + this.toString()
                            + "  socket=" + socket.toString()
                            + "  " + e.toString());
                }
            }
            try {
                socket.close();
//                SST.clear();
                SST.remove(this);
                Log.i(TAG, "run: 断开连接 "
                        + this.toString()
                        + "  socket=" + socket.toString());
            } catch (IOException e) {
                e.printStackTrace();
                Log.e(TAG, "ServerSocketThread run IOException "
                        + this.toString()
                        + "  socket=" + socket.toString()
                        + "  " + e.toString());
            }
        }
    }

}
