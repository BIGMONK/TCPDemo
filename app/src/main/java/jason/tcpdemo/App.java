package jason.tcpdemo;

import android.app.Application;

import jason.tcpdemo.netty.NettyTCPClient;

/**
 * Created by djf on 2017/9/18.
 */

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        NettyTCPClient.init();
    }
}
