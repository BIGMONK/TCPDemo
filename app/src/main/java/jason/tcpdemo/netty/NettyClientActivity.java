package jason.tcpdemo.netty;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import jason.tcpdemo.R;

public class NettyClientActivity extends Activity
        implements NettyClient.ChannelChangeListener {
    private static final String TAG = "NettyClientActivity";
    @BindView(R.id.tv)
    TextView tv;
    private NettyClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_netty_client);
        ButterKnife.bind(this);

        final String ip = "192.168.0.103";
        final int port = 5000;
//         String ip="115.29.198.179";
//        int port=9001;


        new Thread(new Runnable() {
            @Override
            public void run() {
                client = new NettyClient(ip, port);
                client.setChannelChangeListener(NettyClientActivity.this);
                client.doConnect();
            }
        }).start();

    }

    @Override
    public void onChannelChangeListener(Object resistance) {
        Log.e(TAG, "onChannelChangeListener: "+resistance.toString() );
    }
}
