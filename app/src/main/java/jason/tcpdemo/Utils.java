package jason.tcpdemo;

import android.util.Log;

import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

/**
 * Created by djf on 2017/9/18.
 */

public class Utils {

    public static String getThreadInfo(Thread thread) {
        if (thread != null) {
            return "Name:" + thread.getName()
                    + "    ThreadGroup:" + thread.getThreadGroup().getName()
                    + "    Priority:" + thread.getPriority()
                    + "    Id:" + thread.getId()
                    + "    State:" + thread.getState();
        }
        return null;
    }
    /**
     * 获取ip地址
     *
     * @return
     */
    public  static String getHostIP() {

        String hostIp = null;
        try {
            Enumeration nis = NetworkInterface.getNetworkInterfaces();
            InetAddress ia = null;
            while (nis.hasMoreElements()) {
                NetworkInterface ni = (NetworkInterface) nis.nextElement();
                Enumeration<InetAddress> ias = ni.getInetAddresses();
                while (ias.hasMoreElements()) {
                    ia = ias.nextElement();
                    if (ia instanceof Inet6Address) {
                        continue;// skip ipv6
                    }
                    String ip = ia.getHostAddress();
                    if (!"127.0.0.1".equals(ip)) {
                        hostIp = ia.getHostAddress();
                        break;
                    }
                }
            }
        } catch (SocketException e) {
            Log.i("ActivityFuncTcpServer", "SocketException");
            e.printStackTrace();
        }
        return hostIp;

    }
}
