package jason.tcpdemo.mina.handler;



import android.util.Log;

import org.apache.mina.core.service.IoHandler;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;

import jason.tcpdemo.mina.service.SocketService;

/**
 * Created by best on 2016/7/20
 */


public class TimeClientHander implements IoHandler {

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
    }

    @Override
    public void sessionCreated(IoSession session)  {
        // TODO Auto-generated method stub
        if (session != null) {
            Log.d(TAG, "client sessionCreated:" + session.toString() + "建立连接");
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