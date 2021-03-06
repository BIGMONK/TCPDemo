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
import android.text.Html;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import jason.tcpdemo.R;
import jason.tcpdemo.Utils;
import jason.tcpdemo.coms.TcpClientRunnable;

public class ActivityFuncTcpClient extends Activity {
    @BindView(R.id.edit_tcpClientIp1)
    EditText editClientIp1;
    @BindView(R.id.edit_tcpClientIp2)
    EditText editClientIp2;
    @BindView(R.id.edit_tcpClientIp3)
    EditText editClientIp3;
    @BindView(R.id.edit_tcpClientIp4)
    EditText editClientIp4;
    @BindView(R.id.edit_tcpClientPort)
    EditText editClientPort;
    @BindView(R.id.edit_tcpClientID)
    EditText editTcpClientID;
    @BindView(R.id.btn_tcpClientRandomID)
    Button btnClientRandom;
    @BindView(R.id.btn_tcpClientConn)
    Button btnStartClient;
    @BindView(R.id.btn_tcpClientClose)
    Button btnCloseClient;
    @BindView(R.id.btn_tcpCleanClientRecv)
    Button btnCleanClientRcv;
    @BindView(R.id.btn_tcpCleanClientSend)
    Button btnCleanClientSend;
    @BindView(R.id.txt_ClientRcv)
    TextView txtRcv;
    @BindView(R.id.tv_local_ip)
    TextView txtLocalIP;
    @BindView(R.id.txt_ClientSend)
    TextView txtSend;
    @BindView(R.id.edit_tcpClientSend)
    EditText editClientSend;
    @BindView(R.id.btn_tcpClientSend)
    Button btnClientSend;
    @BindView(R.id.btn_ScanDevices)
    Button btnScanDevices;
    @BindView(R.id.listview_Devices)
    ListView listviewDevices;
    @BindView(R.id.atv_tcpClientSend)
    AutoCompleteTextView acTextView;

    private String TAG = "ActivityFuncTcpClient";
    @SuppressLint("StaticFieldLeak")
    public static Context context;
    private static TcpClientRunnable tcpClient = null;
    private final MyHandler myHandler = new MyHandler(this);
    private MyBroadcastReceiver myBroadcastReceiver = new MyBroadcastReceiver();
    ExecutorService exec = Executors.newCachedThreadPool();//无界线程池，可以进行自动线程回收
    private String clientSendMsg;

    private String[] res = {"Keepalive"
            , "Ayoutuvip2", "Bkm19302017"
            , "AOffice-youtu", "Byoutukeji"
            , "Ared", "B488698112"
            , "ATPlink", "B12345678"
    };

    @OnClick({R.id.edit_tcpClientIp1, R.id.edit_tcpClientPort, R.id.edit_tcpClientID,
            R.id.btn_tcpClientRandomID, R.id.btn_tcpClientConn, R.id.btn_tcpClientClose,
            R.id.btn_tcpCleanClientRecv, R.id.btn_tcpCleanClientSend,
            R.id.edit_tcpClientSend, R.id.btn_tcpClientSend, R.id.btn_ScanDevices})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.edit_tcpClientIp1:
                break;
            case R.id.edit_tcpClientPort:
                break;
            case R.id.edit_tcpClientID:
                break;
            case R.id.btn_tcpClientConn://连接
                btnStartClient.setEnabled(false);
                btnCloseClient.setEnabled(true);
                btnClientSend.setEnabled(true);

                String ipString1 = 0 + editClientIp1.getText().toString().trim();
                String ipString2 = 0 + editClientIp2.getText().toString().trim();
                String ipString3 = 0 + editClientIp3.getText().toString().trim();
                String ipString4 = 0 + editClientIp4.getText().toString().trim();

                int ip1Int1 = Integer.parseInt(ipString1);
                if (ip1Int1 <= 0) {
                    editClientIp1.setText("0");
                } else if (ip1Int1 > 255) {
                    editClientIp1.setText("255");
                }
                int ip1Int2 = Integer.parseInt(ipString2);
                if (ip1Int2 <= 0) {
                    editClientIp2.setText("0");
                } else if (ip1Int2 > 255) {
                    editClientIp2.setText("255");
                }
                int ip1Int3 = Integer.parseInt(ipString3);
                if (ip1Int3 <= 0) {
                    editClientIp3.setText("0");
                } else if (ip1Int3 > 255) {
                    editClientIp3.setText("255");
                }
                int ip1Int4 = Integer.parseInt(ipString4);
                if (ip1Int4 <= 0) {
                    editClientIp4.setText("0");
                } else if (ip1Int4 > 255) {
                    editClientIp4.setText("255");
                }
                String ip = ip1Int1 + "." + ip1Int2 + "." + ip1Int3 + "." + ip1Int4;

                String portString = 0 + editClientPort.getText().toString();
                int portInt = Integer.parseInt(portString);


                if (TextUtils.isEmpty(portString) || portInt > 65535 || portInt < 1024) {
                    portString = "" + 1234;
                    editClientPort.setText(portString);
                }

//                ip="47.92.101.44";portInt=9001;
                tcpClient = new TcpClientRunnable(ip, portInt);
                exec.execute(tcpClient);
                break;
            case R.id.btn_tcpClientClose://断开
                tcpClient.closeSelf();
                btnStartClient.setEnabled(true);
                btnCloseClient.setEnabled(false);
                btnClientSend.setEnabled(false);
                break;
            case R.id.btn_tcpCleanClientRecv://清空接收区
                txtRcv.setText("");
                txtRcv.scrollTo(0, 0);
                break;
            case R.id.btn_tcpCleanClientSend://清空发送区
                txtSend.setText("");
                txtSend.scrollTo(0, 0);
                break;
            case R.id.btn_tcpClientRandomID:
                break;
            case R.id.btn_tcpClientSend:
                Message tcpClientSendMessage = Message.obtain();
                tcpClientSendMessage.what = 2;
//                clientSendMsg = editClientSend.getText().toString();
                clientSendMsg = acTextView.getText().toString();
                if (TextUtils.isEmpty(clientSendMsg)) {
                    clientSendMsg = new Date().toString() + "  " + System.currentTimeMillis();
                }
//                clientSendMsg += "(From:" + tcpClient.getLocalSocketAdd() + ")";
                tcpClientSendMessage.obj = clientSendMsg;
                myHandler.sendMessage(tcpClientSendMessage);
                exec.execute(new Runnable() {
                    @Override
                    public void run() {
                        tcpClient.send(clientSendMsg);
                    }
                });
                acTextView.setText("");
                break;
        }

    }


    private class MyHandler extends Handler {
        private WeakReference<ActivityFuncTcpClient> mActivity;

        MyHandler(ActivityFuncTcpClient activity) {
            mActivity = new WeakReference<ActivityFuncTcpClient>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            if (mActivity != null) {
                switch (msg.what) {
                    case 1://接收区
                        if (txtRcv.getLineCount() > 20) {
                            txtRcv.setText("");
                            txtRcv.scrollTo(0, 0);
                        }
//                        txtRcv.append(Utils.simpleDateFormat.format(System.currentTimeMillis())
// +":"+msg.obj.toString() + "\r\n");
                        txtRcv.append(Html.fromHtml("<font color='yellow' size='20'>" + Utils
                                .simpleDateFormat.format(System.currentTimeMillis()) + ":" +
                                "</font>" +
                                "<font color='blue' size='30'>" + msg.obj.toString() + "</font>"
                                + "<br />"));
                        int lines = txtRcv.getLineCount();
                        int offset = lines * txtRcv.getLineHeight();
                        if (offset > txtRcv.getHeight()) {
//                            txtRcv.scrollTo(0, offset - txtRcv.getHeight());
                            txtRcv.scrollBy(0, txtRcv.getLineHeight());
                        }
                        break;
                    case 2://发送区
                        if (txtSend.getLineCount() > 20) {
                            txtSend.setText("");
                            txtSend.scrollTo(0, 0);
                        }
                        txtSend.append(Html.fromHtml("<font color='red' size='20'>" + Utils
                                .simpleDateFormat.format(System.currentTimeMillis()) + ":" +
                                "</font>" +
                                "<font color='blue' size='30'>" + msg.obj.toString() + "</font>"
                                + "<br />"));
//                        txtSend.append(Utils.simpleDateFormat.format(System.currentTimeMillis()
// )+":"+msg.obj.toString() + "\r\n");

                        int lines2 = txtSend.getLineCount();
                        int offset2 = lines2 * txtSend.getLineHeight();
                        if (offset2 > txtSend.getHeight()) {
//                            txtSend.scrollTo(0, offset2 - txtSend.getHeight());
                            txtSend.scrollBy(0, txtSend.getLineHeight());
                        }
                        break;
                    case 3://心跳包
                        myHandler.sendEmptyMessageDelayed(3, 4000);
                        if (tcpClient != null) tcpClient.send(res[0]);

                        if (txtSend.getLineCount() > 20) {
                            txtSend.setText("");
                            txtSend.scrollTo(0, 0);
                        }
                        txtSend.append(Html.fromHtml("<font color='red' size='20'>" + Utils
                                .simpleDateFormat.format(System.currentTimeMillis()) + ":" +
                                "</font>" +
                                "<font color='green' size='30'>" + res[0] + "</font>" + "<br />"));
                        int lines3 = txtSend.getLineCount();
                        int offset3 = lines3 * txtSend.getLineHeight();
                        if (offset3 > txtSend.getHeight()) {
                            txtSend.scrollBy(0, txtSend.getLineHeight());
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
                case "tcpClientReceiver":
                    String msg = intent.getStringExtra("tcpClientReceiver");
                    Message tcpClientReceiverMessage = Message.obtain();
                    tcpClientReceiverMessage.what = 1;
                    tcpClientReceiverMessage.obj = msg;
                    myHandler.sendMessage(tcpClientReceiverMessage);
                    int flag = intent.getIntExtra("flag", 0);
                    switch (flag) {
                        case 1:
                            myHandler.sendEmptyMessage(3);
                            break;
                        case 2:
                            myHandler.removeCallbacksAndMessages(null);
                            break;
                    }
                    break;
            }
        }
    }


    private int getPort(String msg) {
        if (msg.equals("")) {
            msg = "1234";
        }
        return Integer.parseInt(msg);
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tcp_client);
        ButterKnife.bind(this);
        context = this;
        txtRcv.setMovementMethod(ScrollingMovementMethod.getInstance());
        btnCloseClient.setEnabled(false);
        btnClientSend.setEnabled(false);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>
                (this, android.R.layout.simple_list_item_1, res);
        acTextView.setAdapter(adapter);
        txtLocalIP.setText("本地IP:"+Utils.getHostIP());
        bindReceiver();
    }

    private void bindReceiver() {
        IntentFilter intentFilter = new IntentFilter("tcpClientReceiver");
        registerReceiver(myBroadcastReceiver, intentFilter);
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(myBroadcastReceiver);
        super.onDestroy();
        if (tcpClient != null)
            tcpClient.closeSelf();
    }
}
