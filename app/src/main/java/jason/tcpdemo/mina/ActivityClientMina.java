package jason.tcpdemo.mina;

import android.app.Activity;

import java.text.SimpleDateFormat;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import jason.tcpdemo.R;
import jason.tcpdemo.mina.service.SocketService;

public class ActivityClientMina extends Activity implements SocketService.MessageReceivedListener,
        NetSocket.onGetSessionListener {
    private static final String TAG = "ActivityClientMina";
    @BindView(R.id.received)
    TextView received;
    private NetSocket netSocket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_mina);
        ButterKnife.bind(this);
        new Thread(new Runnable() {
            @Override
            public void run() {
                netSocket = new NetSocket();
                netSocket.setOnGetSessionListener(ActivityClientMina.this);
//                netSocket.Connect("115.29.198.179", 9001);
                netSocket.Connect("192.168.0.103", 5000);
                netSocket.getSocketService().setMessageReceivedListener(ActivityClientMina.this);
                while (netSocket.mMSession!=null) {
                    String clientSendMsg = " {\"sub\":\"101\",\"cmd\":\"2000\"}";
                    netSocket.sendMessageSocket(clientSendMsg);
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();


    }

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");


    @Override
    public void onMessageReceived(final Object message) {
        Log.e(TAG, "onMessageReceived: " + message.toString());
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                received.setText(message.toString() + "  " + sdf.format(System.currentTimeMillis()));
            }
        });
    }

    @Override
    public void onSessionClosed() {
        Log.e(TAG, "onSessionClosed ");
//        netSocket.Connect("192.168.0.103", 5000);

    }

    @Override
    public void onGetSession(int flag) {
        Log.e(TAG, "onGetSession: " + flag);
/*
public static final int NET_ERR = 999;
    public static final int SERVER_ERR = 400;
    public static final int CONNECT_SUCCESS = 200;
    public static final int RECONNECT_Failed = 404;
 */
    }
}
