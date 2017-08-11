package jason.tcpdemo.mina.service;


import com.google.gson.Gson;

/**
 * Description:
 * Author：Giousa
 * Date：2016/8/8
 * Email：giousa@chinayoutu.com
 */
public class SocketService {

     public interface MessageReceivedListener {
        void onMessageReceived(Object message);

        void onSessionClosed();
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

    public void processSessionClosed() {
        if (mMessageReceivedListener != null) {
            mMessageReceivedListener.onSessionClosed();
        }
    }
}
