package jason.tcpdemo;

import android.util.Log;

import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.Enumeration;

/**
 * Created by djf on 2017/9/18.
 */

public class Utils {

    public   static SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");



    private static char[] HexCode = {'0', '1', '2', '3', '4', '5', '6', '7',
            '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    /**
     * byte2HexString
     *
     * @param b
     * @return
     */
    public static String byte2HexString(byte b) {
        StringBuffer buffer = new StringBuffer();
        buffer.append(HexCode[(b >>> 4) & 0x0f]);
        buffer.append(HexCode[b & 0x0f]);
        return buffer.toString();
    }

    public static String byte2HexString(byte[] b,int l) {
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < (l<b.length?l:b.length); i++) {
            buffer.append(byte2HexString(b[i]));
        }
        return buffer.toString();
    }

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
    public static String getHostIP() {

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
