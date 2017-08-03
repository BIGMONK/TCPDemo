package jason.tcpdemo.coms;

import android.content.Intent;
import android.util.Log;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;

import jason.tcpdemo.funcs.ActivityFuncTcpClient;

/**
 * Created by Jason Zhu on 2017-04-25.
 * Email: cloud_happy@163.com
 */

public class TcpClientRunnable implements Runnable {
    private String TAG = "TcpClientRunnable";
    private String serverIP = "192.168.88.141";
    private int serverPort = 1234;
    private PrintWriter pw;
    private InputStream is;
    private DataInputStream dis;
    private boolean isRun = true;
    private Socket socket = null;
    byte buff[] = new byte[4096];
    private String rcvMsg;
    private int rcvLen;

    public TcpClientRunnable(String ip, int port) {
        this.serverIP = ip;
        this.serverPort = port;
    }

    public void closeSelf() {
        isRun = false;
        try {
            if (pw != null)
                pw.close();
            if (is != null)
                is.close();
            if (dis != null)
                dis.close();
            if (socket != null)
                socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void send(String msg) {
        if (socket != null && socket.isConnected()) {
            pw.println(msg);
            pw.flush();
        } else {
            Log.e(TAG, "send: socket.isConnected()=" + socket.isConnected());
        }
    }

    @Override
    public void run() {

        createSocket();


    }

    boolean isWhile;

    private void createSocket() {
        if (isRun) {
            Intent intent = new Intent();
            intent.setAction("tcpClientReceiver");
            intent.putExtra("tcpClientReceiver", "正在连接到服务器……");
            ActivityFuncTcpClient.context.sendBroadcast(intent);//将消息发送给主界面
            isWhile = true;
            try {
                socket = new Socket(serverIP, serverPort);
                socket.setSoTimeout(5000);
                Log.e(TAG, "run: 服务器已连接" + socket.toString()
                        + "  " + socket.getRemoteSocketAddress().toString());
                pw = new PrintWriter(socket.getOutputStream(), true);
                is = socket.getInputStream();
                dis = new DataInputStream(is);
            } catch (IOException e) {
                e.printStackTrace();
                Log.e(TAG, "run: 连接服务器( new Socket)失败  " + this.toString()
                        + "  " + e.toString());
                try {
                    Thread.sleep(5000);//重新创建socke的时间间隔
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
                createSocket();
            }
        }
        while (isRun && isWhile) {
            try {
                rcvLen = dis.read(buff);
                if (rcvLen > 0) {
                    rcvMsg = new String(buff, 0, rcvLen, "utf-8");
                    Log.i(TAG, "run: 收到消息: rcvLen=" + rcvLen + "   rcvMsg=" + rcvMsg + "  " + dis.toString());
                    Intent intent = new Intent();
                    intent.setAction("tcpClientReceiver");
                    intent.putExtra("tcpClientReceiver", rcvMsg);
                    ActivityFuncTcpClient.context.sendBroadcast(intent);//将消息发送给主界面
                    if (rcvMsg.equals("QuitClient")) {   //服务器要求客户端结束
                        isRun = false;
                    }
                } else {
                    Log.i(TAG, "run: 收到消息:服务器关闭时read不阻塞  "
                            + "  rcvLen=" + rcvLen
                            + "  dis=" + dis.toString()
                            + "  is=" + is.toString()
                    );
                    Intent intent = new Intent();
                    intent.setAction("tcpClientReceiver");
                    intent.putExtra("tcpClientReceiver", "服务器已断开");
                    ActivityFuncTcpClient.context.sendBroadcast(intent);//将消息发送给主界面
                    isWhile = false;
                    Thread.sleep(50);
                    createSocket();

                }

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                Log.e(TAG, "run: " + this.toString()
                        + "  dis=" + dis.toString()
                        + "  UnsupportedEncodingException=" + e.toString()
                );
            } catch (IOException e) {
                e.printStackTrace();
                Log.e(TAG, "run: " + this.toString()
                        + "  dis=" + dis.toString()
                        + "  IOException=" + e.toString());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        closeSelf();

    }

}
