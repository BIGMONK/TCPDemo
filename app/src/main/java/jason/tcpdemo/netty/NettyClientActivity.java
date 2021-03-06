package jason.tcpdemo.netty;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import jason.tcpdemo.R;
import jason.tcpdemo.Utils;

public class NettyClientActivity extends Activity
        implements ChannelChangeListener {
    private static final String TAG = "NettyClientActivity";

    @BindView(R.id.received)
    TextView received;
    @BindView(R.id.tv_local_ip)
    TextView tvLocalIP;
    @BindView(R.id.tv_jump)
    TextView tvJump;
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
    @BindView(R.id.tv_sent)
    TextView tvSent;
    @BindView(R.id.btn_wifi_set)
    Button btnWifiSet;
    @BindView(R.id.et_wifi_info)
    EditText etWifiInfo;
   @BindView(R.id.et_wifi_info_get)
    EditText etWifiInfoGet;
    @BindView(R.id.btn_wifi_get)
    Button btnWifiGet;
    @BindView(R.id.tv_wifi_info)
    TextView tvWifiInfo;
    private NettyTCPClient client;
    private int portInt;
    private String ip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_netty_client);
        ButterKnife.bind(this);

        tvLocalIP.setText(Utils.getHostIP());
    }

    long time;
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy.mm.dd hh:MM:ss.SSS");


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
        ip = ip1Int1 + "." + ip1Int2 + "." + ip1Int3 + "." + ip1Int4;

        String portString = 0 + editTcpClientPort.getText().toString();
        portInt = Integer.parseInt(portString);
        client = NettyTCPClient.getInstance();
        client.setChannelChangeListener(NettyClientActivity.this);
        client.connect(ip, portInt, true, 2, 5, true);

    }

    @Override
    protected void onResume() {
        super.onResume();
        NettyTCPClient.getInstance().setChannelChangeListener(NettyClientActivity.this);
    }

    @Override
    public void onChannelChangeListenerReceive(ChannelHandlerContext ctx, final Object msg) {
        Log.d(TAG, "$$$$$onChannelChangeListenerReceive接收数据:"
                + msg.toString() + "  长度：" + msg.toString().length()
                + "  线程信息：" + getThreadInfo(Thread.currentThread())
        );
        synchronized (received.getClass()) {
            received.post(new Runnable() {
                @Override
                public void run() {
                    time = System.currentTimeMillis();
                    received.setText("接收时间（" + sdf.format(time) + "):" + msg.toString());
                    if (!msg.toString().equals("07070077")){
                        tvWifiInfo.setText(msg.toString());
                    }
                }
            });
        }
    }

    @Override
    public void onChannelChangeListenerSend(final Object resistance) {
        Log.d(TAG, "$$$$$onChannelChangeListenerSend发送数据: "
                + resistance.toString()
                + "  长度：" + resistance.toString().length()
                + "  线程信息：" + getThreadInfo(Thread.currentThread())
        );
        synchronized (tvSent.getClass()) {
            tvSent.post(new Runnable() {
                @Override
                public void run() {
                    time = System.currentTimeMillis();
                    tvSent.setText("发送时间（" + sdf.format(time) + "):" + resistance.toString());
                }
            });
        }
    }

    /**
     * 连接成功
     *
     * @param ch
     */
    @Override
    public void onConnectActivity(final ChannelHandlerContext ch) {
        Log.d(TAG, "$$$$$连接成功:"
                + ch.name() + "  "
                + ch.channel().toString()
                + "  线程信息：" + getThreadInfo(Thread.currentThread())
        );
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                connect.setText("连接成功" + ch.channel().toString());
                connect.setClickable(true);
            }
        });
//        try {
//            sendData("{\"sub\":\"10\",\"cmd\":\"1000\",\"data\":{\"type\":0,\"code\":\"ac:83:f3:42:09:c0\"}}");
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    /**
     * 连接断开
     *
     * @param ch
     */
    @Override
    public void onConnectInactivity(ChannelHandlerContext ch) {
        Log.d(TAG, "$$$$$连接断开:"
                + ch.name() + "  "
                + ch.channel().toString()
                + "  线程信息：" + getThreadInfo(Thread.currentThread())
        );
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                connect.setText("连接已断开，点击重连");
                connect.setClickable(true);
            }
        });

    }

    /**
     * 连接失败回调
     *
     * @param future
     * @param times
     */
    @Override
    public void onConnectFailed(ChannelFuture future, final Object times) {
        Log.d(TAG, "$$$$$onConnectFailed: 连接失败："
                + future.channel().toString()
                + "  次数：" + times
                + "  线程信息：" + getThreadInfo(Thread.currentThread())
        );
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                connect.setText("连接失败" + times + ",即将重连");
            }
        });
    }

    /**
     * 停止连接重试
     *
     * @param future
     */
    @Override
    public void onreConnectStop(ChannelFuture future) {

        Log.d(TAG, "$$$$$onreConnectStop: 停止连接"
                + "  " + future.channel().toString()
                + "  线程信息：" + getThreadInfo(Thread.currentThread())

        );

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                connect.setText("失败次数过多，点击重试");
                connect.setClickable(true);
            }
        });
    }

    @Override
    public void onStartConnecting(String ip, int port) {

        Log.d(TAG, "$$$$$onStartConnecting: 开始连接"
                + "  ip:" + ip + "  port:" + port
                + "  线程信息：" + getThreadInfo(Thread.currentThread())
        );

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                connect.setText("连接中……");
                connect.setClickable(false);
            }
        });
    }

    /**
     * 其他状态状态监听
     *
     * @param flag
     */
    @Override
    public void onNettyClientStatusListen(int flag) {
        Log.d(TAG, "onNettyClientStatusListen: " + flag);
    }


    private String getThreadInfo(Thread thread) {
        if (thread != null) {
            return "Name:" + thread.getName()
                    + "    ThreadGroup:" + thread.getThreadGroup().getName()
                    + "    Priority:" + thread.getPriority()
                    + "    Id:" + thread.getId()
                    + "    State:" + thread.getState();
        }
        return null;
    }

    @OnClick({R.id.send, R.id.tv_jump, R.id.btn_wifi_set, R.id.btn_wifi_get})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_wifi_set:
                String setInfo = etWifiInfo.getText().toString().trim();
                if (TextUtils.isEmpty(setInfo)) {
                    Toast.makeText(this, "设置内容不能为空", Toast.LENGTH_SHORT).show();
                } else {
                    sendData(setInfo);
                }
                break;
            case R.id.btn_wifi_get:
                String getInfo = etWifiInfoGet.getText().toString().trim();
                if (TextUtils.isEmpty(getInfo)) {
                    Toast.makeText(this, "获取指令不能为空", Toast.LENGTH_SHORT).show();
                } else {
                    sendData(getInfo);
                }
                break;
            case R.id.send:
                long t = System.currentTimeMillis();
                sent.setText("" + t);
                sendData("" + t);
                break;
            case R.id.tv_jump:
                startActivity(new Intent(this, Main2Activity.class));
                break;
        }
    }

    private void sendData(String s) {
        if (client != null) {
            client.sendData(s);
        } else {
            Toast.makeText(this, "服务器未连接", Toast.LENGTH_SHORT).show();
        }
    }


}
