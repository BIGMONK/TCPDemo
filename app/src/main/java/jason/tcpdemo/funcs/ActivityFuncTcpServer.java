package jason.tcpdemo.funcs;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Date;
import java.util.Enumeration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import jason.tcpdemo.R;
import jason.tcpdemo.Utils;
import jason.tcpdemo.coms.TcpServerRunnable;

import static android.R.attr.offset;

public class ActivityFuncTcpServer extends Activity {
    @BindView(R.id.txt_Server_Ip)
    TextView txtServerIp;
    @BindView(R.id.edit_Server_Port)
    EditText editServerPort;
    @BindView(R.id.edit_Server_ID)
    EditText editServerRandom;
    @BindView(R.id.btn_tcpServerRandomID)
    Button btnServerRandom;
    @BindView(R.id.btn_tcpServerConn)
    Button btnStartServer;
    @BindView(R.id.btn_tcpServerClose)
    Button btnCloseServer;
    @BindView(R.id.btn_tcpCleanServerRecv)
    Button btnCleanServerRcv;
    @BindView(R.id.btn_tcpCleanServerSend)
    Button btnCleanServerSend;
    @BindView(R.id.txt_ServerRcv)
    TextView txtRcv;
    @BindView(R.id.txt_ServerSend)
    TextView txtSend;
    @BindView(R.id.edit_tcpClientSend)
    EditText editServerSend;
    @BindView(R.id.btn_tcpServerSend)
    Button btnServerSend;
    private static TcpServerRunnable tcpServer = null;
    private final MyHandler myHandler = new MyHandler(this);
    private MyBroadcastReceiver myBroadcastReceiver = new MyBroadcastReceiver();
    @SuppressLint("StaticFieldLeak")
    public static Context context;
    ExecutorService exec = Executors.newCachedThreadPool();
    private String serverSendMsg;

    @OnClick({R.id.txt_Server_Ip, R.id.edit_Server_Port, R.id.edit_Server_ID,
            R.id.btn_tcpServerRandomID, R.id.btn_tcpServerConn, R.id.btn_tcpServerClose,
            R.id.btn_tcpCleanServerRecv, R.id.btn_tcpCleanServerSend, R.id.txt_ServerRcv,
            R.id.txt_ServerSend, R.id.edit_tcpClientSend, R.id.btn_tcpServerSend})
    public void onViewClicked(View view) {
        switch (view.getId()) {

            case R.id.txt_Server_Ip:
                break;
            case R.id.edit_Server_Port:
                break;
            case R.id.edit_Server_ID:
                break;
            case R.id.txt_ServerRcv:
                break;
            case R.id.txt_ServerSend:
                break;
            case R.id.edit_tcpClientSend:
                break;
            case R.id.btn_tcpServerConn:
                Log.i("A", "onClick: 开始");
                btnStartServer.setEnabled(false);
                btnCloseServer.setEnabled(true);
                btnServerSend.setEnabled(true);
                tcpServer = new TcpServerRunnable(getHost(editServerPort.getText().toString()));
                exec.execute(tcpServer);
                break;
            case R.id.btn_tcpServerClose:
                tcpServer.closeSelf();
                btnStartServer.setEnabled(true);
                btnCloseServer.setEnabled(false);
                btnServerSend.setEnabled(false);
                break;
            case R.id.btn_tcpCleanServerRecv:
                txtRcv.setText("");
                txtRcv.scrollTo(0,0);

                break;
            case R.id.btn_tcpCleanServerSend:
                txtSend.setText("");
                txtSend.scrollTo(0,0);

                break;
            case R.id.btn_tcpServerRandomID:
                break;
            case R.id.btn_tcpServerSend:
                Message message = Message.obtain();
                message.what = 2;
                serverSendMsg = editServerSend.getText().toString();
                if (TextUtils.isEmpty(serverSendMsg)) {
                    serverSendMsg = new Date().toString() + "  " + System.currentTimeMillis();
                }
                serverSendMsg+="(From:"+tcpServer.getLocalSocketAdd()+")";
                message.obj = serverSendMsg;
                myHandler.sendMessage(message);
                exec.execute(new Runnable() {
                    @Override
                    public void run() {
                        for (int i = 0; i <tcpServer.SST.size() ; i++) {
                            TcpServerRunnable.ServerSocketThread thread = tcpServer.SST.get(i);
                            if (thread.socket.isConnected()){
                                thread.send(serverSendMsg);
                            }
                        }
                    }
                });
                break;
        }
    }


    private class MyHandler extends Handler {
        private final WeakReference<ActivityFuncTcpServer> mActivity;

        MyHandler(ActivityFuncTcpServer activity) {
            mActivity = new WeakReference<ActivityFuncTcpServer>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            ActivityFuncTcpServer activity = mActivity.get();
            if (activity != null) {
                switch (msg.what) {
                    case 1://接收区
                        if (txtRcv.getLineCount()>20){
                            txtRcv.setText("");
                            txtRcv.scrollTo(0,0);
                        }
                        txtRcv.append(msg.obj.toString() + "\r\n");
                        int lines = txtRcv.getLineCount();
                        int offset = lines * txtRcv.getLineHeight();
                        if (offset > txtRcv.getHeight()) {
//                            txtRcv.scrollTo(0, offset - txtRcv.getHeight());
                            txtRcv.scrollBy(0,txtRcv.getLineHeight());
                        }
                        break;
                    case 2:

                        if (txtSend.getLineCount()>20){
                            txtSend.setText("");
                            txtSend.scrollTo(0,0);
                        }

                        txtSend.append(msg.obj.toString() + "\r\n");
                        int lines2 = txtSend.getLineCount();
                        int offset2 = lines2 * txtSend.getLineHeight();
                        if (offset2 > txtSend.getHeight()) {
//                            txtSend.scrollTo(0, offset2 - txtSend.getHeight());
                            txtSend.scrollBy(0,txtSend.getLineHeight());

                        }
                        break;
                }
            }
        }
    }

    private class MyBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String mAction = intent.getAction();
            switch (mAction) {
                case "tcpServerReceiver":
                    String msg = intent.getStringExtra("tcpServerReceiver");
                    Message message = Message.obtain();
                    message.what = 1;
                    message.obj = msg;
                    myHandler.sendMessage(message);
                    break;
            }
        }
    }

    private void bindReceiver() {
        IntentFilter intentFilter = new IntentFilter("tcpServerReceiver");
        registerReceiver(myBroadcastReceiver, intentFilter);
    }

    private int getHost(String msg) {
        if (msg.equals("")) {
            msg = "1234";
        }
        return Integer.parseInt(msg);
    }


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tcp_server);
        ButterKnife.bind(this);
        context = this;

        bindReceiver();

        btnCloseServer.setEnabled(false);
        btnServerSend.setEnabled(false);
        txtServerIp.setText(Utils.getHostIP());
    }




    @Override
    protected void onDestroy() {
        unregisterReceiver(myBroadcastReceiver);
        super.onDestroy();

    }
}
