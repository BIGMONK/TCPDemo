package jason.tcpdemo.mina.handler;
import android.util.Log;
import android.widget.Toast;

import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.service.IoHandler;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;

import java.net.InetSocketAddress;

import jason.tcpdemo.mina.KeepAliveMessageFactoryImpl;
import jason.tcpdemo.mina.service.SocketService;
public class TimeClientHander implements IoHandler {
    public static String Request = "{\"sub\":\"101\",\"cmd\":\"2000\"}";
    public static String Response = "{\"sub\":\"101\",\"data\":{\"code\":0},\"cmd\":\"2000\"}";
    private final String TAG = TimeClientHander.class.getSimpleName();
    private SocketService mSocketService = null;

    public TimeClientHander() {
    }

    public TimeClientHander(SocketService socketService) {
        mSocketService = socketService;
    }

    @Override
    public void exceptionCaught(IoSession arg0, Throwable arg1)
            throws Exception {
        // TODO Auto-generated method stub
        Log.d(TAG, "异常捕获exceptionCaught arg0"+arg0.toString());
        Log.d(TAG, "异常捕获exceptionCaught arg1"+arg1.getMessage());
        arg1.printStackTrace();
    }

    @Override
    public void messageReceived(IoSession arg0, Object message) throws Exception {
        // TODO Auto-generated method stub
        Log.d(TAG, "client接受信息:" + mSocketService.toString()
                + "数据内容："+message.toString()
                + "数据内容字节长度："+message.toString().getBytes().length
        );
        if (mSocketService != null) {
            mSocketService.processMessageReceived(message);
        }
    }

    @Override
    public void messageSent(IoSession arg0, Object message) throws Exception {
        // TODO Auto-generated method stub
        if (arg0 != null) {
            Log.d(TAG, "client发送信息:" + arg0.toString()+ "   " + message.toString());
        }
        if (mSocketService != null) {
            mSocketService.processMessageSent(message);
        }
    }

    @Override
    public void sessionClosed(IoSession session) throws Exception {
        // TODO Auto-generated method stub
        if (session != null)
            Log.d(TAG, "client sessionClosed" + session.toString());
        if (mSocketService != null) {
            mSocketService.processSessionClosed();
        }
        Log.d("session","连接断开");
    }

    @Override
    public void sessionCreated(IoSession session)  {
        // TODO Auto-generated method stub
        if (session != null) {
            Log.d(TAG,"建立连接sessionCreated:" + session.toString() );
        }
        if (mSocketService != null) {
            mSocketService.processSessionCreated(session);
        }
    }

    @Override
    public void sessionIdle(IoSession session, IdleStatus status) throws Exception {
        // TODO Auto-generated method stub
        if (session != null) {
            Log.d(TAG, "session="+session.toString()+"   IDLE " + session.getIdleCount(status));
//            session.write(Request);//空闲的时候发送心跳包
        }

    }

    @Override
    public void sessionOpened(IoSession arg0)  {
        // TODO Auto-generated method stub
        Log.d(TAG, "打开连接sessionOpened:"+arg0.toString());
        if (mSocketService != null) {
            mSocketService.processSessionOpened(arg0);
        }
    }

}