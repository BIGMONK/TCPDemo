package jason.tcpdemo.mina.service;


import android.util.Log;

import com.google.gson.Gson;

import org.apache.mina.core.session.IoSession;



/**
 * Description:
 * Author：Giousa
 * Date：2016/8/8
 * Email：giousa@chinayoutu.com
 */
public class SocketService {
    private static final String TAG = "SocketService";
    public interface MessageReceivedListener {
        void onMessageReceived(Object message);

        void onMessageSent(Object message);

        void onSessionClosed();

        void onSessionCreated(IoSession session);

        void onSessionOpened(IoSession arg0);
    }

    private MessageReceivedListener mMessageReceivedListener;

    public void setMessageReceivedListener(MessageReceivedListener messageReceivedListener) {
        mMessageReceivedListener = messageReceivedListener;
    }

    public void processMessageReceived(Object message) {
        if (mMessageReceivedListener != null) {
            mMessageReceivedListener.onMessageReceived(message);
        }
    }

    public void processMessageSent(Object message) {
        if (mMessageReceivedListener != null) {
            mMessageReceivedListener.onMessageSent(message);
        }
    }

    public void processSessionClosed() {
        if (mMessageReceivedListener != null) {
            mMessageReceivedListener.onSessionClosed();
        }
    }

    public void processSessionCreated(IoSession session) {
        if (mMessageReceivedListener != null) {
            mMessageReceivedListener.onSessionCreated(session);
        }
    }

    public void processSessionOpened(IoSession arg0) {
        if (mMessageReceivedListener != null) {
            Log.d(TAG, "打开连接processSessionOpened:"+arg0.toString());

            mMessageReceivedListener.onSessionOpened(arg0);
        }
    }
}
