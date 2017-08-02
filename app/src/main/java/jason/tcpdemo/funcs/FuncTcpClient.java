package jason.tcpdemo.funcs;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Handler;

import jason.tcpdemo.R;
import jason.tcpdemo.ScanDeviceTool;
import jason.tcpdemo.coms.TcpClient;


/**
 * Created by Jason Zhu on 2017-04-24.
 * Email: cloud_happy@163.com
 */

public class FuncTcpClient extends Activity {
    private String TAG = "FuncTcpClient";
    @SuppressLint("StaticFieldLeak")
    public static Context context;
    private Button btnStartClient, btnCloseClient, btnCleanClientSend, btnCleanClientRcv, btnClientSend, btnClientRandom, btnScanDevices;
    private TextView txtRcv, txtSend;
    private EditText editClientSend, editClientID, editClientPort, editClientIp;
    private static TcpClient tcpClient = null;
    private MyBtnClicker myBtnClicker = new MyBtnClicker();
    private final MyHandler myHandler = new MyHandler(this);
    private MyBroadcastReceiver myBroadcastReceiver = new MyBroadcastReceiver();
    ExecutorService exec = Executors.newCachedThreadPool();
    private String clientSendMsg;

    private class MyBtnClicker implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.btn_ScanDevices:

                    break;
                case R.id.btn_tcpClientConn:
                    Log.i(TAG, "onClick: 开始");
                    btnStartClient.setEnabled(false);
                    btnCloseClient.setEnabled(true);
                    btnClientSend.setEnabled(true);
                    tcpClient = new TcpClient(editClientIp.getText().toString(), getPort(editClientPort.getText().toString()));
                    exec.execute(tcpClient);
                    break;
                case R.id.btn_tcpClientClose:
                    tcpClient.closeSelf();
                    btnStartClient.setEnabled(true);
                    btnCloseClient.setEnabled(false);
                    btnClientSend.setEnabled(false);
                    break;
                case R.id.btn_tcpCleanClientRecv:
                    txtRcv.setText("");
                    break;
                case R.id.btn_tcpCleanClientSend:
                    txtSend.setText("");
                    break;
                case R.id.btn_tcpClientRandomID:
                    break;
                case R.id.btn_tcpClientSend:
                    Message tcpClientSendMessage = Message.obtain();
                    tcpClientSendMessage.what = 2;
                    clientSendMsg = editClientSend.getText().toString();
                    if (TextUtils.isEmpty(clientSendMsg)) {
                        clientSendMsg = new Date().toString() + "  " + System.currentTimeMillis();
                    }
                    tcpClientSendMessage.obj = clientSendMsg;
                    myHandler.sendMessage(tcpClientSendMessage);
                    exec.execute(new Runnable() {
                        @Override
                        public void run() {
                            tcpClient.send(clientSendMsg);
                        }
                    });
                    break;
            }
        }
    }

    private class MyHandler extends android.os.Handler {
        private WeakReference<FuncTcpClient> mActivity;

        MyHandler(FuncTcpClient activity) {
            mActivity = new WeakReference<FuncTcpClient>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            if (mActivity != null) {
                switch (msg.what) {
                    case 1:
                        txtRcv.append(msg.obj.toString()+ "\r\n");
                        int lines = txtRcv.getLineCount();
                        int offset = lines * txtRcv.getLineHeight();
                        if (offset > txtRcv.getHeight()) {
                            txtRcv.scrollTo(0, offset - txtRcv.getHeight());
                        }
                        break;
                    case 2:
                        txtSend.append(msg.obj.toString() + "\r\n");
                        int lines2 = txtSend.getLineCount();
                        int offset2 = lines2 * txtSend.getLineHeight();
                        if (offset2 > txtSend.getHeight()) {
                            txtSend.scrollTo(0, offset2 - txtSend.getHeight());
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
        context = this;
        bindID();
        bindListener();
        bindReceiver();
        Ini();
    }


    private void bindID() {
        btnStartClient = (Button) findViewById(R.id.btn_tcpClientConn);
        btnScanDevices = (Button) findViewById(R.id.btn_ScanDevices);
        btnCloseClient = (Button) findViewById(R.id.btn_tcpClientClose);
        btnCleanClientRcv = (Button) findViewById(R.id.btn_tcpCleanClientRecv);
        btnCleanClientSend = (Button) findViewById(R.id.btn_tcpCleanClientSend);
        btnClientRandom = (Button) findViewById(R.id.btn_tcpClientRandomID);
        btnClientSend = (Button) findViewById(R.id.btn_tcpClientSend);
        editClientPort = (EditText) findViewById(R.id.edit_tcpClientPort);
        editClientIp = (EditText) findViewById(R.id.edit_tcpClientIp);
        editClientSend = (EditText) findViewById(R.id.edit_tcpClientSend);
        txtRcv = (TextView) findViewById(R.id.txt_ClientRcv);
        txtRcv.setMovementMethod(ScrollingMovementMethod.getInstance());
        txtSend = (TextView) findViewById(R.id.txt_ClientSend);
    }

    private void bindListener() {
        btnScanDevices.setOnClickListener(myBtnClicker);
        btnStartClient.setOnClickListener(myBtnClicker);
        btnCloseClient.setOnClickListener(myBtnClicker);
        btnCleanClientRcv.setOnClickListener(myBtnClicker);
        btnCleanClientSend.setOnClickListener(myBtnClicker);
        btnClientRandom.setOnClickListener(myBtnClicker);
        btnClientSend.setOnClickListener(myBtnClicker);
    }

    private void bindReceiver() {
        IntentFilter intentFilter = new IntentFilter("tcpClientReceiver");
        registerReceiver(myBroadcastReceiver, intentFilter);
    }

    private void Ini() {
        btnCloseClient.setEnabled(false);
        btnClientSend.setEnabled(false);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        tcpClient.closeSelf();

    }
}
