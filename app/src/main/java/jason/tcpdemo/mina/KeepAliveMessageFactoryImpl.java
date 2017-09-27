package jason.tcpdemo.mina;

import android.util.Log;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.keepalive.KeepAliveMessageFactory;

/**
 * Created by djf on 2017/9/19.
 */

public class KeepAliveMessageFactoryImpl implements KeepAliveMessageFactory {
    public static String Request = "{\"sub\":\"101\",\"cmd\":\"2000\"}";
    public static String Response = "{\"sub\":\"101\",\"data\":{\"code\":0},\"cmd\":\"2000\"}";
    private static final String TAG = "KeepAliveMessageFactory";

    @Override
    public boolean isRequest(IoSession ioSession, Object o) {
        Log.d(TAG, "isRequest: ");
        if (o.toString().equals(Request)) {
            Log.d(TAG, "请求心跳isRequest: " + o.toString());
            return true;
        }
        return false;
    }

    @Override
    public boolean isResponse(IoSession ioSession, Object o) {
        Log.d(TAG, "isResponse: ");
        if (o.toString().equals(Response)) {
            Log.d(TAG, "响应心跳isResponse: " + o.toString());
            return true;
        }
        return false;
    }

    @Override
    public Object getRequest(IoSession ioSession) {
        Log.d(TAG, "请求预设信息getRequest: ");
        return Request;
    }

    @Override
    public Object getResponse(IoSession ioSession, Object o) {
        Log.d(TAG, "响应预设信息getResponse: ");
//        return Response;
        return null;
    }
}
