package jason.tcpdemo.netty;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import jason.tcpdemo.R;

public class NettyClientActivity extends Activity
        implements NettyTCPClient.ChannelChangeListener {
    private static final String TAG = "NettyClientActivity";

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
    @BindView(R.id.tv_sent)
    TextView tvSent;
    private NettyTCPClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_netty_client);
        ButterKnife.bind(this);
    }

    long time;
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy.mm.dd hh:MM:ss.SSS");

    @OnClick(R.id.send)
    public void send() {
        if (client != null) {
            try {
                long t = System.currentTimeMillis();
                sent.setText("" + t);
                client.sendData("" + t);
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


        new Thread(new Runnable() {
            @Override
            public void run() {
                client = new NettyTCPClient(ip, portInt);
                client.setChannelChangeListener(NettyClientActivity.this);
                client.doConnect();
            }
        }).start();
    }


    @Override
    public void onChannelChangeListenerReceive(ChannelHandlerContext ctx, final Object msg) {
        Log.d(TAG, "$$$$$onChannelChangeListenerReceive接收数据: "
                + msg.toString() + "  长度：" + msg.toString().length()
                +"  线程信息："+ getThreadInfo(Thread.currentThread())
        );
        synchronized (received.getClass()) {
            received.post(new Runnable() {
                @Override
                public void run() {
                    time = System.currentTimeMillis();
                    received.setText("接收时间（" + sdf.format(time) + "):" + msg.toString());
                }
            });
        }
    }

    @Override
    public void onChannelChangeListenerSend(final Object resistance) {
        Log.d(TAG, "$$$$$onChannelChangeListenerSend发送数据: "
                + resistance.toString()
                + "  长度：" + resistance.toString().length()
                +"  线程信息："+ getThreadInfo(Thread.currentThread())
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
     * @param ch
     */
    @Override
    public void onConnectActivity(ChannelHandlerContext ch) {
        Log.d(TAG, "$$$$$连接成功:"
                + ch.name()+"  "
                + ch.channel().toString()
                +"  线程信息："+ getThreadInfo(Thread.currentThread())
        );

        connect.setText("连接成功");

    }

    /**
     * 连接断开
     * @param ch
     */
    @Override
    public void onConnectInactivity(ChannelHandlerContext ch) {

        Log.d(TAG, "$$$$$连接断开:"
                + ch.name()+"  "
                + ch.channel().toString()
                +"  线程信息："+ getThreadInfo(Thread.currentThread())
        );

        connect.setText("连接已断开，点击重连");
        connect.setClickable(true);
    }

    /**
     * 连接失败回调
     * @param future
     * @param times
     */
    @Override
    public void onConnectFailed(ChannelFuture future,Object times) {
        Log.d(TAG, "$$$$$onConnectFailed: 连接失败："
               + future.channel().toString()
                + "  次数：" + times
                +"  线程信息："+ getThreadInfo(Thread.currentThread())
        );

        connect.setText("连接失败"+times+",即将重连");

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
                +"  线程信息："+ getThreadInfo(Thread.currentThread())

        );

        connect.setText("失败次数过多，点击重试");
        connect.setClickable(true);
    }

    @Override
    public void onStartConnecting(String ip, int port) {

        Log.d(TAG, "$$$$$onStartConnecting: 开始连接"
                + "  ip:" + ip+"  port:"+port
                +"  线程信息："+ getThreadInfo(Thread.currentThread())
        );

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                connect.setText("连接中……");
                connect.setClickable(false);
            }
        });
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

}
