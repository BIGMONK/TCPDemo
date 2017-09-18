package jason.tcpdemo.netty;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;


import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import jason.tcpdemo.R;

import static jason.tcpdemo.Utils.getThreadInfo;

public class Main2Activity extends AppCompatActivity implements ChannelChangeListener {
    private static final String TAG = "Main2Activity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        Log.d(TAG, "onCreate: ");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: ");
    }

    @Override
    protected void onResume() {
        super.onResume();
        NettyTCPClient.getInstance().setChannelChangeListener(this);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public void onChannelChangeListenerReceive(ChannelHandlerContext ctx, Object msg) {
        Log.d(TAG, "$$$$$onChannelChangeListenerReceive接收数据: "
                + msg.toString() + "  长度：" + msg.toString().length()
                + "  线程信息：" + getThreadInfo(Thread.currentThread())
        );
    }
    @Override
    public void onChannelChangeListenerSend(Object resistance) {
        Log.d(TAG, "$$$$$onChannelChangeListenerSend发送数据: "
                + resistance.toString()
                + "  长度：" + resistance.toString().length()
                + "  线程信息：" + getThreadInfo(Thread.currentThread())
        );
    }

    @Override
    public void onConnectActivity(ChannelHandlerContext ch) {

    }

    @Override
    public void onConnectInactivity(ChannelHandlerContext ch) {

    }

    @Override
    public void onConnectFailed(ChannelFuture future, Object resistance) {

    }

    @Override
    public void onreConnectStop(ChannelFuture future) {

    }

    @Override
    public void onStartConnecting(String ip, int port) {

    }

    @Override
    public void onNettyClientStatusListen(int flag) {

    }
}
