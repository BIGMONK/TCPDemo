package jason.tcpdemo.coms;

import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.ConnectException;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.Date;

import jason.tcpdemo.Utils;
import jason.tcpdemo.funcs.ActivityFuncTcpClient;

public class TcpClientRunnable implements Runnable {
    private String TAG = "TcpClientRunnable";
    private String serverIP = "192.168.88.141";
    private int serverPort = 1234;
    private PrintWriter pw;
    private InputStream is;
    private DataInputStream dis;
    private boolean isRun = true;

    public String getLocalSocketAdd() {
        if (socket == null) {
            return "socket=null";
        }
        return socket.getLocalSocketAddress().toString();
    }

    private Socket socket = null;
    byte buff[] = new byte[4096];
    private String rcvMsg;
    private int rcvLen;
    private Intent intent;

    public TcpClientRunnable(String ip, int port) {
        this.serverIP = ip;
        this.serverPort = port;
    }

    public void closeSelf() {
        isRun = false;
        try {
            if (pw != null)
                pw.close();
            if (dis != null)
                dis.close();
            if (is != null)
                is.close();
            if (socket != null) {
                socket.shutdownInput();
                socket.shutdownOutput();
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void send(String msg) {
        if (socket != null && pw != null) {
            pw.print(msg);
            pw.flush();
            Log.d(TAG, "已发送数据send: " + msg);
        } else {
            Log.d(TAG, "send: socket 未连接");
        }
    }

    @Override
    public void run() {

        createSocket();


    }

    boolean isWhile;

    private void createSocket() {
        if (isRun) {
            if (intent == null) {
                intent = new Intent();
                intent.setAction("tcpClientReceiver");
            }
            intent.putExtra("tcpClientReceiver", "正在连接服务器:" + serverIP + ":" + serverPort
                    + "…" + Utils.simpleDateFormat.format(System.currentTimeMillis()));
            intent.putExtra("flag", 0);
            ActivityFuncTcpClient.context.sendBroadcast(intent);//将消息发送给主界面
            isWhile = true;
            try {
                socket = new Socket(serverIP, serverPort);
                socket.setSoTimeout(5000);
                if (intent == null) {
                    intent = new Intent();
                    intent.setAction("tcpClientReceiver");
                }
                intent.putExtra("flag", 1);
                intent.putExtra("tcpClientReceiver", "连接服务器成功:" + serverIP + ":"
                        + serverPort + "…" + Utils.simpleDateFormat.format(System
                        .currentTimeMillis()));
                ActivityFuncTcpClient.context.sendBroadcast(intent);//将消息发送给主界面
                Log.d(TAG, "run: 连接服务器成功" + socket.toString()
                        + "  " + socket.getRemoteSocketAddress().toString());
                pw = new PrintWriter(socket.getOutputStream(), true);
                is = socket.getInputStream();
                dis = new DataInputStream(is);
            } catch (ConnectException e) {
                e.printStackTrace();
                Log.d(TAG, "run: 连接服务器失败:" + serverIP + ":"
                        + serverPort + "…" + this.toString()
                        + "  " + e.toString());
                if (intent == null) {
                    intent = new Intent();
                    intent.setAction("tcpClientReceiver");
                }
                intent.putExtra("tcpClientReceiver", "连接服务器失败:" + serverIP + ":"
                        + serverPort + "…" + new Date().toString());
                intent.putExtra("flag", 0);
                ActivityFuncTcpClient.context.sendBroadcast(intent);//将消息发送给主界面
                try {
                    Thread.sleep(5000);//重新创建socke的时间间隔
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
                createSocket();
            } catch (SocketException e) {
                e.printStackTrace();
            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        while (isRun && isWhile && dis != null) {
            try {
                rcvLen = dis.read(buff);
                if (rcvLen > 0) {
                    rcvMsg = new String(buff, 0, rcvLen, "utf-8");
                    Log.i(TAG, "run: 收到消息: rcvLen=" + rcvLen
                            + "   rcvMsg=" + rcvMsg
                            + "  " + dis.available()
                            + "   " + Utils.byte2HexString(buff, rcvLen)
                    );


                    if (intent == null) {
                        intent = new Intent();
                        intent.setAction("tcpClientReceiver");
                    }
                    intent.putExtra("tcpClientReceiver", rcvMsg+"--->"+ Utils.byte2HexString(buff, rcvLen));
                    intent.putExtra("flag", 0);
                    ActivityFuncTcpClient.context.sendBroadcast(intent);//将消息发送给主界面
                    if (rcvMsg.equals("QuitClient")) {   //服务器要求客户端结束
                        isRun = false;
                    }
                } else {
                    Log.i(TAG, "run: 收到消息:服务器关闭时read不阻塞 read返回-1  "
                            + "  rcvLen=" + rcvLen
                            + "  dis=" + dis.toString()
                            + "  is=" + is.toString()
                    );
                    if (intent == null) {
                        intent = new Intent();
                        intent.setAction("tcpClientReceiver");
                    }
                    intent.putExtra("tcpClientReceiver", "服务器已断开" + "…" + Utils.simpleDateFormat
                            .format(System.currentTimeMillis()));
                    intent.putExtra("flag", 2);
                    ActivityFuncTcpClient.context.sendBroadcast(intent);//将消息发送给主界面
                    isWhile = false;
                    Thread.sleep(50);
                    createSocket();
                }

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                Log.d(TAG, "run: " + this.toString()
                        + "  dis=" + dis.toString()
                        + "  UnsupportedEncodingException=" + e.toString()
                );
            } catch (InterruptedException e) {
                e.printStackTrace();
                Log.d(TAG, "run: " + this.toString()
                        + "  dis=" + dis.toString()
                        + "  InterruptedException=" + e.toString());
            } catch (SocketTimeoutException e) {
                e.printStackTrace();
                Log.d(TAG, "run: " + this.toString()
                        + "  dis=" + dis.toString()
                        + "  SocketTimeoutException=" + e.toString()
                        + "  e.getCause()=" + e.getCause()
                        + "  e.getMessage()=" + e.getMessage());
            } catch (SocketException e) {
                e.printStackTrace();
                Log.d(TAG, "run: " + this.toString()
                        + "  dis=" + dis.toString()
                        + "  SocketException=" + e.toString()
                        + "  e.getCause()=" + e.getCause()
                        + "  e.getMessage()=" + e.getMessage()
                );

                String msg = e.getMessage();
                if (!TextUtils.isEmpty(msg) && msg.equals("Socket closed")) {
                    if (intent == null) {
                        intent = new Intent();
                        intent.setAction("tcpClientReceiver");
                    }
                    intent.putExtra("tcpClientReceiver", "客户端已断开连接");
                    intent.putExtra("flag", 2);
                    ActivityFuncTcpClient.context.sendBroadcast(intent);//将消息发送给主界面
                }

            } catch (IOException e) {
                e.printStackTrace();
                Log.d(TAG, "run: " + this.toString()
                        + "  dis=" + dis.toString()
                        + "  IOException=" + e.toString());
            }
        }
        closeSelf();

    }

}
