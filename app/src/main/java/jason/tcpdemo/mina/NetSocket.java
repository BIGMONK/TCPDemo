package jason.tcpdemo.mina;


import android.util.Log;

import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.service.IoConnector;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.prefixedstring.PrefixedStringCodecFactory;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

import java.net.InetSocketAddress;
import java.nio.charset.Charset;

import io.netty.handler.codec.string.StringDecoder;
import jason.tcpdemo.mina.handler.TimeClientHander;
import jason.tcpdemo.mina.service.SocketService;

/**
 * Description:
 * Author：Giousa
 * Date：2016/8/8
 * Email：giousa@chinayoutu.com
 */
public class NetSocket {

    private final static String TAG = "NetSocket";
    public IoSession mMSession;
    private final IoConnector mConnector;

    private SocketService mSocketService = null;
    private ConnectFuture connectFuture;

    public NetSocket() {
        mConnector = new NioSocketConnector();
        mConnector.getFilterChain().addLast("logger", new LoggingFilter());
        mConnector.getFilterChain().addLast("codec",
                new ProtocolCodecFilter(new PrefixedStringCodecFactory()));
        mSocketService = new SocketService();
        TimeClientHander timeClientHander = new TimeClientHander(mSocketService);
        mConnector.setHandler(timeClientHander);
    }

    private int mConnectTime;

    public void Connect(String ip, int port) {
        //如果已经存在socket连接，断开
        if (mMSession != null) {
            if (mMSession.isConnected()) {
                Log.d(TAG, " mMSession.before close   mMSession=" + mMSession);
                mMSession.close(true);
                Log.d(TAG, " mMSession.after close   mMSession=" + mMSession);
            }
        }
        connectFuture = mConnector.connect(new InetSocketAddress(ip, port));
        Log.d(TAG, "connectFuture  connectFuture=" + connectFuture.toString() +
                "  mConnector=" + mConnector.toString()
        );
        // 等待建立连接
        connectFuture.awaitUninterruptibly();
        Log.d(TAG, "connectFuture.awaitUninterruptibly   mConnector=" + mConnector.toString());
        if (connectFuture.isConnected()) {
            mMSession = connectFuture.getSession();
            if (mMSession == null) {
                listener.onGetSession(SERVER_ERR);
                Log.d(TAG, "connectFuture mMSession == null  mConnector=" + mConnector.toString());
                if (((++mConnectTime) <= 3)) {
                    Log.d(TAG, "服务器连接失败mMSession == null,重连" + mConnectTime);
                    Connect(ip, port);
                } else {
                    mConnectTime = 0;
                    listener.onGetSession(RECONNECT_Failed);
                }
            } else {
                listener.onGetSession(CONNECT_SUCCESS);
                Log.d(TAG, "连接成功:connectFuture=" + connectFuture + "  mMSession=" + mMSession.toString());
                mConnectTime = 0;

//                mMSession.getCloseFuture().awaitUninterruptibly();// 等待连接断开

            }
        } else {
            Log.d(TAG, "connectFuture is not Connected mConnector111=" + mConnector.toString());
            listener.onGetSession(SERVER_ERR);
            if (((++mConnectTime) <= 3)) {
                Log.d(TAG, "服务器Connected()连接失败又重连" + mConnectTime);
                Connect(ip, port);
            } else {
                mConnectTime = 0;
                listener.onGetSession(RECONNECT_Failed);
            }

        }

    }

    public static final int NET_ERR = 999;
    public static final int SERVER_ERR = 400;
    public static final int CONNECT_SUCCESS = 200;
    public static final int RECONNECT_Failed = 404;

    public void setOnGetSessionListener(onGetSessionListener listener) {
        this.listener = listener;
    }

    private onGetSessionListener listener;

    public interface onGetSessionListener {
        void onGetSession(int flag);
    }

    public void sendMessageSocket(String msg) {
        if (mMSession != null)
            mMSession.write(msg);
        else
            Log.d(TAG, "mMSession=" + null);
    }

    public SocketService getSocketService() {
        return mSocketService;
    }

}
