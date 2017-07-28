package jason.tcpdemo.coms;

import android.content.Intent;
import android.util.Log;

import java.io.BufferedReader;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;

import jason.tcpdemo.funcs.FuncTcpClient;
import jason.tcpdemo.funcs.FuncTcpServer;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.T;

/**
 * Created by Jason Zhu on 2017-04-25.
 * Email: cloud_happy@163.com
 */

public class TcpClient implements Runnable {
    private String TAG = "TcpClient";
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


    public TcpClient(String ip, int port) {
        this.serverIP = ip;
        this.serverPort = port;
    }

    public void closeSelf() {
        isRun = false;
    }

    public void send(String msg) {
        pw.println(msg);
        pw.flush();
    }




    @Override
    public void run() {

        while (isRun) {
            while (socket == null || !socket.isConnected()) {
                try {
                    Log.d(TAG, "run:  new Socket");
                    socket = new Socket(serverIP, serverPort);
//            socket.setSoTimeout(5000);
                    pw = new PrintWriter(socket.getOutputStream(), true);
                    is = socket.getInputStream();
                    dis = new DataInputStream(is);
                    Log.d(TAG, "run: 服务器已连接");

                } catch (ConnectException e) {
                    e.printStackTrace();
                    Log.d(TAG, "run: 连接服务器失败");
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            try {
                if (dis != null) {
                    rcvLen = dis.read(buff);
                    if (rcvLen > 0) {
                        rcvMsg = new String(buff, 0, rcvLen, "utf-8");
                        Log.i(TAG, "run: 收到消息:" + rcvMsg);
                        Intent intent = new Intent();
                        intent.setAction("tcpClientReceiver");
                        intent.putExtra("tcpClientReceiver", rcvMsg);
                        FuncTcpClient.context.sendBroadcast(intent);//将消息发送给主界面
                        if (rcvMsg.equals("QuitClient")) {   //服务器要求客户端结束
                            isRun = false;
                        }
                    }
                } else {
                    Thread.sleep(1000);
                    Log.d(TAG, "run: 休眠");
                }

            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
        try {
            pw.close();
            is.close();
            dis.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
