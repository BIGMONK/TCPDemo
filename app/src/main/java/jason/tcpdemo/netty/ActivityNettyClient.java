package jason.tcpdemo.netty;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import jason.tcpdemo.R;

public class ActivityNettyClient extends Activity
        implements NettyClient.ChannelChangeListener {
    private static final String TAG = "ActivityNettyClient";

    @BindView(R.id.received)
    TextView received;
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
    @BindView(R.id.address)
    LinearLayout address;
    @BindView(R.id.connect)
    Button connect;
    @BindView(R.id.send)
    Button send;
    @BindView(R.id.sent)
    TextView sent;
    private NettyClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_netty_client);
        ButterKnife.bind(this);

//        final String ip = "192.168.0.103";
//        final int port = 5000;
        //         String ip="115.29.198.179";
        //        int port=9001;



    }

    @Override
    public void onChannelChangeListener(Object resistance) {
        Log.e(TAG, "onChannelChangeListener: " + resistance.toString());
        received.setText(resistance.toString());
    }

    @OnClick(R.id.send)
    public void send(){
        if (client!=null){
            try {
                long t= System.currentTimeMillis();
                sent.setText(""+t);
                client.sendData(""+t);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    @OnClick(R.id.connect)
    public void onViewClicked() {

        String ipString1 = 0 + editTcpClientIp1.getText().toString().trim();
        String ipString2 = 0 + editTcpClientIp2.getText().toString().trim();
        String ipString3 = 0 + editTcpClientIp3.getText().toString().trim();
        String ipString4 = 0 + editTcpClientIp4.getText().toString().trim();

        int ip1Int1 = Integer.parseInt(ipString1);
        if (ip1Int1<=0){
            editTcpClientIp1.setText("0");
        }else if (ip1Int1>255){
            editTcpClientIp1.setText("255");
        }
        int ip1Int2 = Integer.parseInt(ipString2);
        if (ip1Int2<=0){
            editTcpClientIp2.setText("0");
        }else if (ip1Int2>255){
            editTcpClientIp2.setText("255");
        }
        int ip1Int3 = Integer.parseInt(ipString3);
        if (ip1Int3<=0){
            editTcpClientIp3.setText("0");
        }else if (ip1Int3>255){
            editTcpClientIp3.setText("255");
        }
        int ip1Int4 = Integer.parseInt(ipString4);
        if (ip1Int4<=0){
            editTcpClientIp4.setText("0");
        }else if (ip1Int4>255){
            editTcpClientIp4.setText("255");
        }
        final String ip = ip1Int1 + "." + ip1Int2 + "." + ip1Int3 + "." + ip1Int4;

        String portString = 0 + editTcpClientPort.getText().toString();
        final int portInt = Integer.parseInt(portString);



        new Thread(new Runnable() {
            @Override
            public void run() {
                client = new NettyClient(ip, portInt);
                client.setChannelChangeListener(ActivityNettyClient.this);
                client.doConnect();
            }
        }).start();
    }
}
