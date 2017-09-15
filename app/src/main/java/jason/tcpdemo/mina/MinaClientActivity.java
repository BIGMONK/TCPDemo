package jason.tcpdemo.mina;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.apache.mina.core.session.IoSession;

import java.text.SimpleDateFormat;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import jason.tcpdemo.R;
import jason.tcpdemo.mina.service.SocketService;

public class MinaClientActivity extends Activity
        implements SocketService.MessageReceivedListener,
        NetSocket.onGetSessionListener {
    private static final String TAG = "MinaClientActivity";
    @BindView(R.id.received)
    TextView received;
    @BindView(R.id.connect)
    Button connect;
    @BindView(R.id.edit_tcpClientIp1)
    EditText editTcpClientIp1;
    @BindView(R.id.edit_tcpClientIp2)
    EditText editTcpClientIp2;
    @BindView(R.id.edit_tcpClientIp3)
    EditText editTcpClientIp3;
    @BindView(R.id.edit_tcpClientIp4)
    EditText editTcpClientIp4;
    @BindView(R.id.edit_tcpClientPort)
    EditText editTcpClientPort;
    private NetSocket netSocket;
    @BindView(R.id.send)
    Button send;
    @BindView(R.id.sent)
    TextView sent;
    private boolean sending;
    private String ip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_mina);
        ButterKnife.bind(this);
    }

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");


    @Override
    public void onMessageReceived(final Object message) {
        Log.d(TAG, "onMessageReceived: " + message.toString());
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                received.setText("接收数据：（时间" + sdf.format(System.currentTimeMillis()) + "）：" + message.toString());
            }
        });
    }

    @Override
    public void onMessageSent(final Object message) {
        Log.d(TAG, "onMessageSent: " + message.toString());

        sent.post(new Runnable() {
            @Override
            public void run() {
                sent.setText("发送数据：（时间" + sdf.format(System.currentTimeMillis()) + "）：" + message.toString());
            }
        });
    }

    @Override
    public void onSessionClosed() {
        Log.d(TAG, "onSessionClosed ");
    }

    @Override
    public void onSessionCreated(IoSession session) {
        Log.d(TAG, "onSessionCreated: " + session.toString());
    }

    @Override
    public void onSessionOpened(IoSession arg0) {
        Log.d(TAG, "onSessionOpened: " + arg0.toString());
    }

    @Override
    public void onGetSession(int flag) {
        Log.d(TAG, "onGetSession: " + flag);
    }

    long t;
    @OnClick(R.id.send)
    public void send() {
        if (netSocket != null) {
            t = System.currentTimeMillis();
            netSocket.sendMessageSocket(t + "");
        }
    }

    @OnClick(R.id.connect)
    public void onViewClicked() {
        String ipString1 = 0 + editTcpClientIp1.getText().toString().trim();
        String ipString2 = 0 + editTcpClientIp2.getText().toString().trim();
        String ipString3 = 0 + editTcpClientIp3.getText().toString().trim();
        String ipString4 = 0 + editTcpClientIp4.getText().toString().trim();

        int ip1Int1 = Integer.parseInt(ipString1);
        if (ip1Int1 <= 0) {
            editTcpClientIp1.setText("0");
        } else if (ip1Int1 > 255) {
            editTcpClientIp1.setText("255");
        }
        int ip1Int2 = Integer.parseInt(ipString2);
        if (ip1Int2 <= 0) {
            editTcpClientIp2.setText("0");
        } else if (ip1Int2 > 255) {
            editTcpClientIp2.setText("255");
        }
        int ip1Int3 = Integer.parseInt(ipString3);
        if (ip1Int3 <= 0) {
            editTcpClientIp3.setText("0");
        } else if (ip1Int3 > 255) {
            editTcpClientIp3.setText("255");
        }
        int ip1Int4 = Integer.parseInt(ipString4);
        if (ip1Int4 <= 0) {
            editTcpClientIp4.setText("0");
        } else if (ip1Int4 > 255) {
            editTcpClientIp4.setText("255");
        }
        final String ip = ip1Int1 + "." + ip1Int2 + "." + ip1Int3 + "." + ip1Int4;

        String portString = 0 + editTcpClientPort.getText().toString();
        final int portInt = Integer.parseInt(portString);


        connect.setClickable(false);
        sending = true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                netSocket = new NetSocket();
                netSocket.setOnGetSessionListener(MinaClientActivity.this);

//                netSocket.Connect("115.29.198.179", 9001);
                netSocket.Connect(ip, portInt);
                netSocket.getSocketService().setMessageReceivedListener(MinaClientActivity.this);
//
//                while (sending) {
//                    String clientSendMsg = " {\"sub\":\"101\",\"cmd\":\"2000\"}";
//                    netSocket.sendMessageSocket(clientSendMsg);
//                    try {
//                        Thread.sleep(5000);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }

            }
        }).start();
    }

    @Override
    protected void onDestroy() {
        sending = false;
        netSocket.mMSession.close(true);
        super.onDestroy();
    }
}
