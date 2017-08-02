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
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
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
import jason.tcpdemo.coms.TcpClient;

public class FuncTcpClient extends Activity {
    @BindView(R.id.edit_tcpClientIp)
    EditText editClientIp;
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
    private String TAG = "FuncTcpClient";
    @SuppressLint("StaticFieldLeak")
    public static Context context;
    private static TcpClient tcpClient = null;
    private final MyHandler myHandler = new MyHandler(this);
    private MyBroadcastReceiver myBroadcastReceiver = new MyBroadcastReceiver();
    ExecutorService exec = Executors.newCachedThreadPool();
    private String clientSendMsg;

    @OnClick({R.id.edit_tcpClientIp, R.id.edit_tcpClientPort, R.id.edit_tcpClientID,
            R.id.btn_tcpClientRandomID, R.id.btn_tcpClientConn, R.id.btn_tcpClientClose,
            R.id.btn_tcpCleanClientRecv, R.id.btn_tcpCleanClientSend,
            R.id.edit_tcpClientSend, R.id.btn_tcpClientSend, R.id.btn_ScanDevices})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.edit_tcpClientIp:
                break;
            case R.id.edit_tcpClientPort:
                break;
            case R.id.edit_tcpClientID:
                break;
            case R.id.btn_tcpClientConn:
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


    private class MyHandler extends Handler {
        private WeakReference<FuncTcpClient> mActivity;

        MyHandler(FuncTcpClient activity) {
            mActivity = new WeakReference<FuncTcpClient>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            if (mActivity != null) {
                switch (msg.what) {
                    case 1:
                        txtRcv.append(msg.obj.toString() + "\r\n");
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
        ButterKnife.bind(this);
        context = this;
        txtRcv.setMovementMethod(ScrollingMovementMethod.getInstance());
        btnCloseClient.setEnabled(false);
        btnClientSend.setEnabled(false);

        bindReceiver();
    }

    private void bindReceiver() {
        IntentFilter intentFilter = new IntentFilter("tcpClientReceiver");
        registerReceiver(myBroadcastReceiver, intentFilter);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (tcpClient != null)
            tcpClient.closeSelf();
    }
}
